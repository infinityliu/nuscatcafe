package models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Blog(
                  _id:Option[BSONObjectID],
                  _creationDate: Option[DateTime],
                  _updateDate: Option[DateTime],
                  userId:String,
                  catId:String,
                  title:String,
                  description:String
                )
object Blog{
  implicit val fmt : Format[Blog] = Json.format[Blog]
  implicit object BlogBSONReader extends BSONDocumentReader[Blog] {
    def read(doc: BSONDocument): Blog = {
      Blog(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("userId").get,
        doc.getAs[String]("catId").get,
        doc.getAs[String]("title").get,
        doc.getAs[String]("description").get)
    }
  }

  implicit object BlogBSONWriter extends BSONDocumentWriter[Blog] {
    def write(blog: Blog): BSONDocument = {
      BSONDocument(
        "_id" -> blog._id,
        "_creationDate" -> blog._creationDate.map(date => BSONDateTime(date.getMillis)),
        "_updateDate" -> blog._updateDate.map(date => BSONDateTime(date.getMillis)),
        "userId" -> blog.userId,
        "catId" -> blog.catId,
        "title" -> blog.title,
        "description" -> blog.description

      )
    }
  }
}