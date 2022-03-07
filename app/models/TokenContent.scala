package models

import play.api.libs.json.Json
import spray.json._

// Create our Post type
case class TokenContent(email:String, isAdmin:String)

object TokenContent {
  // We're going to be serving this type as JSON, so specify a
  // default Json formatter for our Post type here
  implicit val format = Json.format[TokenContent]
//  implicit object BetterPersonFormat extends JsonFormat[TokenContent] {
//    // deserialization code
//    override def read(json: JsValue): TokenContent = {
//      val fields = json.asJsObject("Person object expected").fields
//      TokenContent(
//        email = fields("email").convertTo[String],
//        password = fields("password").convertTo[String]
//      )
//    }
//
//    // serialization code
//    override def write(person: TokenContent): JsValue = JsObject(
//      "email" -> person.email.toJson,
//      "password" -> person.password.toJson
//    )
//  }
//  implicit object TokenContentJsonFormat extends RootJsonFormat[TokenContent] {
//
//    override def read(json: JsValue): TokenContent = {
//
//      val jsObject = json.asJsObject
//      val jsFields = jsObject.fields
//
//      val i = jsFields.ge
//      val d = jsFields.get("d").map(_.convertTo[Double]).getOrElse(0d)
//
//      TokenContent(i, d)
//    }
//
//    override def write(obj: Test): JsValue = serializationError("not supported")
//  }
}
