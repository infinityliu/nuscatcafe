package controllers

import javax.inject.Inject
import models.User
import models.TokenContent
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc._
import repositories.UserRepository
import utilities.JwtUtility
import spray.json.JsValue

import scala.concurrent.{ExecutionContext, Future}


case class UserRequest[A](token: TokenContent, request: Request[A]) extends WrappedRequest(request)

//class SecuredAuthenticator @Inject()(jwtUtility: utilities.JwtUtility) extends BaseController {
//  implicit val formatUserDetails = Json.format[User]

class SecuredAuthenticator @Inject()(bodyParser: BodyParsers.Default, jwtUtility: utilities.JwtUtility, val userRepository: UserRepository)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {
  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  // A regex for parsing the Authorization header value
  private val headerTokenRegex = """Bearer (.+?)""".r
  def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
//    val jwtToken = extractBearerToken(request).getOrElse("notoken")
    val jwtToken = request.headers.get("jw_token").getOrElse("")
    println(jwtToken)
//    println(jwtUtility.decodePayload(jwtToken))

    if (jwtUtility.isValidToken(jwtToken)) {
      val decodedToken = jwtUtility.decodePayload(jwtToken)
//      val t = decodedToken.asJsObject.getFields("email").head.toString().drop(1).dropRight(1)
//      println(t)
      val tokenContent = new TokenContent(decodedToken.asJsObject.fields.get("email").head.toString().drop(1).dropRight(1),decodedToken.asJsObject.fields.get("isAdmin").head.toString().drop(1).dropRight(1))
      block(UserRequest(tokenContent, request))

//      userRepository.Validate(t).map( res => block(UserRequest(res, request)))
    } else {
      println(jwtToken)
      Future.successful(Results.Unauthorized("Invalid credential"))
    }
  }

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }


}
