package repositories

import models.Cat

import javax.inject._
import reactivemongo.api.bson.collection.BSONCollection
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import org.joda.time.DateTime
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

import scala.util.{Failure, Success}


@Singleton
class CatRepository @Inject()(
                                 implicit executionContext: ExecutionContext,
                                 reactiveMongoApi: ReactiveMongoApi
                               ) {
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(db => db.collection("cats"))

  def findAll(limit: Int = 100): Future[Seq[Cat]] = {

    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Cat])
        .cursor[Cat](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Cat]]())
    )
  }

  def findOne(id: BSONObjectID): Future[Option[Cat]] = {
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Cat]).one[Cat])
  }

//  def findTopPopularCats(): Future[Seq[Cat]] = {
//  }
//
//  def catBlogsPlusOne(id: String): Future[WriteResult] = {
//    val objectIdTryResult = BSONObjectID.parse(id).get
//    val cat = null
//    findOne(objectIdTryResult).map {
//      result => update(objectIdTryResult, result.get)
//    }
//  }

  def create(cat: Cat): Future[WriteResult] = {
    collection.flatMap(_.insert(ordered = false)
      .one(cat.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime()))))
  }

  def update(id: BSONObjectID, cat: Cat):Future[WriteResult] = {

    collection.flatMap(
      _.update(ordered = false).one(BSONDocument("_id" -> id),
        cat.copy(
          _updateDate = Some(new DateTime())))
    )
  }

  def delete(id: BSONObjectID):Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }


}
