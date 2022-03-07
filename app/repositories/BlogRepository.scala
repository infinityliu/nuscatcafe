package repositories

import models.Blog

import javax.inject._
import reactivemongo.api.bson.collection.BSONCollection
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import org.joda.time.DateTime
import reactivemongo.api.commands.WriteResult

//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
//
//import reactivemongo.api.bson.{ BSONDocument, BSONString }
//
//import reactivemongo.api.Cursor
//import reactivemongo.api.bson.collection.BSONCollection


@Singleton
class BlogRepository @Inject()(
                               implicit executionContext: ExecutionContext,
                               reactiveMongoApi: ReactiveMongoApi,
                               val catRepository: CatRepository
                             ) {
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(db => db.collection("blogs"))

  def findAll(limit: Int = 100): Future[Seq[Blog]] = {

    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Blog])
        .cursor[Blog](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Blog]]())
    )
  }

  def findOne(id: BSONObjectID): Future[Option[Blog]] = {
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Blog]).one[Blog])
  }

  def findByCatId(catId: String): Future[Seq[Blog]] = {
    collection.flatMap(_.find(BSONDocument("catId" -> catId), Option.empty[Blog]).cursor[Blog](ReadPreference.Primary)
      .collect[Seq](100, Cursor.FailOnError[Seq[Blog]]()))
  }

  def findByUserId(userId: String): Future[Seq[Blog]] = {
    collection.flatMap(_.find(BSONDocument("userId" -> userId), Option.empty[Blog]).cursor[Blog](ReadPreference.Primary)
      .collect[Seq](100, Cursor.FailOnError[Seq[Blog]]()))
  }

//  def findTopPopularCats(): Future[Seq[String]] = {
//    val clo = collection.value.get.get
//    import clo.BatchCommands.AggregationFramework.{
//      AggregationResult, Group, Match, SumField
//    }
//
//    val res: Future[AggregationResult] = col.aggregate(
//      Group(BSONString("$state"))( "totalPop" -> SumField("population")),
//      List(Match(BSONDocument("totalPop" -> BSONDocument("$gte" -> 10000000L)))))
//
//    res.map(_.documents)
//  }

  def create(blog: Blog): Future[WriteResult] = {
//    catRepository.update(blog.catId)
    collection.flatMap(_.insert(ordered = false)
      .one(blog.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime()))))
  }

  def update(id: BSONObjectID, blog: Blog):Future[WriteResult] = {

    collection.flatMap(
      _.update(ordered = false).one(BSONDocument("_id" -> id),
        blog.copy(
          _updateDate = Some(new DateTime())))
    )
  }

  def delete(id: BSONObjectID):Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }


}