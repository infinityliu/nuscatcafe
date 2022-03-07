package models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._


case class Cat(
                 _id:Option[BSONObjectID],
                 _creationDate: Option[DateTime],
                 _updateDate: Option[DateTime],
                 name:String,
                 age:Int,
                 gender:String,
                 description:String,
                 location:String,
                 healthCondition:String,
                 isAlive:Boolean,
                 isSterilized:Boolean
               )
object Cat{
  implicit val fmt : Format[Cat] = Json.format[Cat]
  implicit object UserBSONReader extends BSONDocumentReader[Cat] {
    def read(doc: BSONDocument): Cat = {
      Cat(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("name").get,
        doc.getAs[Int]("age").get,
        doc.getAs[String]("gender").get,
        doc.getAs[String]("description").get,
        doc.getAs[String]("location").get,
        doc.getAs[String]("healthCondition").get,
        doc.getAs[Boolean]("isAlive").get,
        doc.getAs[Boolean]("isSterilized").get)
    }
  }

  implicit object UserBSONWriter extends BSONDocumentWriter[Cat] {
    def write(cat: Cat): BSONDocument = {
      BSONDocument(
        "_id" -> cat._id,
        "_creationDate" -> cat._creationDate.map(date => BSONDateTime(date.getMillis)),
        "_updateDate" -> cat._updateDate.map(date => BSONDateTime(date.getMillis)),
        "name" -> cat.name,
        "age" -> cat.age,
        "gender" -> cat.gender,
        "description" -> cat.description,
        "location" -> cat.location,
        "healthCondition" -> cat.healthCondition,
        "isAlive" -> cat.isAlive,
        "isSterilized" -> cat.isSterilized,
      )
    }
  }
}
