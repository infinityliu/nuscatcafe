
package controllers

import javax.inject._
import play.api.mvc._
import repositories.CatRepository
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, __}
import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}

import models.Cat
import play.api.libs.json.JsValue

@Singleton
class CatController @Inject()(
                                 implicit executionContext: ExecutionContext,
                                 val catRepository: CatRepository,
                                 val controllerComponents: ControllerComponents,
                                 authAction: SecuredAuthenticator)
  extends BaseController {
  def findAll():Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    catRepository.findAll().map {
      cats => Ok(Json.toJson(cats))
    }
  }

  def findOne(id:String):Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => catRepository.findOne(objectId).map {
        cat => Ok(Json.toJson(cat))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the cat id"))
    }
  }

  def create():Action[JsValue] = authAction.async(controllerComponents.parsers.json) { implicit request => {

    request.body.validate[Cat].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      cat =>
        if (request.token.isAdmin != "true") {
          Future.successful(Unauthorized("Unauthorized to create the cat"))
        }
        else {
          catRepository.create(cat).map {
            _ => Created(Json.toJson(cat))
          }
        }
    )
  }}

  def update(id: String):Action[JsValue]  = authAction.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[Cat].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      cat =>{
        val objectIdTryResult = BSONObjectID.parse(id)
        if (request.token.isAdmin != "true") {
          Future.successful(Unauthorized("Unauthorized to update the cat"))
        }
        else {
          objectIdTryResult match {
            case Success(objectId) => catRepository.update(objectId, cat).map {
              result => Ok(Json.toJson(result.ok))
            }
            case Failure(_) => Future.successful(BadRequest("Cannot parse the cat id"))
          }
        }
      }
    )
  }}

  def delete(id: String):Action[AnyContent]  = authAction.async { implicit request => {
    val objectIdTryResult = BSONObjectID.parse(id)
    if (request.token.isAdmin != "true") {
      Future.successful(Unauthorized("Unauthorized to delete the cat"))
    }
    else {
      objectIdTryResult match {
        case Success(objectId) => catRepository.delete(objectId).map {
          _ => NoContent
        }
        case Failure(_) => Future.successful(BadRequest("Cannot parse the cat id"))
      }
    }
  }}
}
