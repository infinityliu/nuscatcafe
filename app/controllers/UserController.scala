package controllers

import javax.inject._
import play.api.mvc._
import repositories.UserRepository
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, __}

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import models.{User, UserLoginDTO}
import play.api.libs.json._
import utilities.JwtUtility

@Singleton
class UserController @Inject()(
                                 implicit executionContext: ExecutionContext,
                                 val userRepository: UserRepository,
                                 val controllerComponents: ControllerComponents,
                                 authAction: SecuredAuthenticator)
  extends BaseController {

  def login(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[UserLoginDTO].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      user =>
        userRepository.Validate(user.email).map {
          returnUser => {
            if (returnUser.get.password == user.password) {
              val userId = returnUser.get._id
              println(userId)
              val payload = s"""{"email":"${user.email}", "isAdmin":"${returnUser.get.isAdmin}"}"""
              Ok(JwtUtility.createToken(payload))
            }
            else {
              NotFound
            }
          }
        }
    )
  }}

  def create():Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request => {

    request.body.validate[User].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      user =>
        userRepository.create(user).map {
          _ => Created(Json.toJson(user))
        }
    )
  }
  }

  def update(id: String): Action[JsValue] = authAction.async(controllerComponents.parsers.json) { implicit request => {
//    println(request.headers)
//    println(request.body)
//    println(request.token)
//    println(request.request)
    request.body.validate[User].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      user => {
        if (user.email != request.token.email) {
//          println(user.email)
//          println(request.token.email)
          Future.successful(Unauthorized("Unauthorized"))
        }
        else {
          val objectIdTryResult = BSONObjectID.parse(id)
          objectIdTryResult match {
            case Success(objectId) => userRepository.update(objectId, user).map {
              result => Ok(Json.toJson(result.ok))
            }
            case Failure(_) => Future.successful(BadRequest("Cannot parse the id"))
          }
        }
      }
    )
  }
  }


//  def update(id: String): Action[JsValue] = authAction.async(controllerComponents.parsers.json) { implicit request => {
//    request.body.validate[User].fold(
//      _ => Future.successful(BadRequest("Cannot parse request body")),
//      user =>
//        userRepository.Validate(user.email).map {
//          returnUser => {
//            if (returnUser.get._id.get == BSONObjectID.parse(id).get) {
//              val payload = s"""{"email":"${user.email}", "password":"${user.password}"}"""
//              Ok(JwtUtility.createToken(payload))
//            }
//            else {
//              NotFound
//            }
//          }
//        }
//    )
//  }
//  }

  def delete(id: String):Action[AnyContent]  = authAction.async { implicit request => {
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => userRepository.delete(objectId).map {
        _ => NoContent
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the user id"))
    }
  }}

}

