package controllers

import javax.inject._
import play.api.mvc._
import repositories.{BlogRepository, UserRepository}
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, __}

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import models.Blog
import play.api.libs.json.JsValue

@Singleton
class BlogController @Inject()(
                                 implicit executionContext: ExecutionContext,
                                 val blogRepository: BlogRepository,
                                 val userRepository: UserRepository,
                                 val controllerComponents: ControllerComponents,
                                 authAction: SecuredAuthenticator)
  extends BaseController {
  def findAll():Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    blogRepository.findAll().map {
      blogs => Ok(Json.toJson(blogs))
    }
  }

  def findOne(id:String):Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => blogRepository.findOne(objectId).map {
        blog => Ok(Json.toJson(blog))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the blog id"))
    }
  }

  def findByCatId(catId:String):Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val objectIdTryResult = BSONObjectID.parse(catId)
    objectIdTryResult match {
      case Success(objectId) => blogRepository.findByCatId(catId).map {
        blogs => Ok(Json.toJson(blogs))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the cat id"))
    }
  }

  def findByUserId(userId:String):Action[AnyContent] = authAction.async { implicit request: Request[AnyContent] =>
    val objectIdTryResult = BSONObjectID.parse(userId)
    objectIdTryResult match {
      case Success(objectId) => blogRepository.findByUserId(userId).map {
        blogs => Ok(Json.toJson(blogs))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the user id"))
    }
  }

  def create():Action[JsValue] = authAction.async(controllerComponents.parsers.json) { implicit request => {

    request.body.validate[Blog].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      blog =>
          blogRepository.create(blog).map {
            _ => Created(Json.toJson(blog))
          }
    )
  }}

  def update(id: String):Action[JsValue]  = authAction.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[Blog].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      blog =>{
        val objectIdTryResult = BSONObjectID.parse(id)
        objectIdTryResult match {
          case Success(objectId) => blogRepository.update(objectId, blog).map {
            result => Ok(Json.toJson(result.ok))
          }
          case Failure(_) => Future.successful(BadRequest("Cannot parse the blog id"))
        }
      }
    )
  }}

  def delete(id: String):Action[AnyContent]  = authAction.async { implicit request => {
    val objectIdTryResult = BSONObjectID.parse(id)
    println(request.token)
    if (request.token.isAdmin != "true") {
      println(request.token.isAdmin)
      Future.successful(Unauthorized("Unauthorized to delete the blog"))
    }
    else {
      objectIdTryResult match {
        case Success(objectId) => blogRepository.delete(objectId).map {
          _ => NoContent
        }
        case Failure(_) => Future.successful(BadRequest("Cannot parse the blog id"))
      }
    }
  }}
}