package utilities

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import org.janjaali.sprayjwt.Jwt
import org.janjaali.sprayjwt.algorithms.HS256
import spray.json.JsValue

import scala.util.Success

class JwtUtility {
  val JwtSecretKey = "secretKey"

  def createToken(payload: String): String = {
    Jwt.encode(payload, JwtSecretKey, HS256).get
  }

  def isValidToken(jwtToken: String): Boolean = {
    if (jwtToken == "") {
      false
    }
    else {
      try {
        Jwt.decode(jwtToken, JwtSecretKey).isSuccess
      }
      catch {
        case e => false}
    }
  }

  def decodePayload(jwtToken: String): JsValue = {
    Jwt.decode(jwtToken, JwtSecretKey).get
  }

}

//class JwtUtility {
//  val JwtSecretKey = "secretKey"
//  val JwtSecretAlgo = "HS256"
//
//  def createToken(payload: String): String = {
//    val header = JwtHeader(JwtSecretAlgo)
//    val claimsSet = JwtClaimsSet(payload)
//
//    JsonWebToken(header, claimsSet, JwtSecretKey)
//  }
//
//  def isValidToken(jwtToken: String): Boolean =
//    JsonWebToken.validate(jwtToken, JwtSecretKey)
//
//  def decodePayload(jwtToken: String): Option[String] =
//    jwtToken match {
//      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
//      case _ => None
//    }
//}


object JwtUtility extends JwtUtility