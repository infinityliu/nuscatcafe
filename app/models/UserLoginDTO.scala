package models

import play.api.libs.json.Json

// Create our Post type
case class UserLoginDTO(email:String, password:String)

object UserLoginDTO {
  // We're going to be serving this type as JSON, so specify a
  // default Json formatter for our Post type here
  implicit val format = Json.format[UserLoginDTO]
}
