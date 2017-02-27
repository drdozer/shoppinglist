package uk.co.turingatemyhamster.shoppinglist.user.impl

import akka.Done
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import uk.co.turingatemyhamster.shoppinglist.user.api.User

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

/**
  *
  *
  * @author Matthew Pocock
  */
class UserRepository(session: CassandraSession)(implicit ec: ExecutionContext) {

  def getUsersForEmail(email: String): Future[immutable.Seq[User]] = {
    session.selectAll(
      """
        |SELECT userId FROM userByEmail
        |WHERE email = ?
      """.stripMargin, email) map { rows =>
      rows.to[immutable.Seq] map { row =>
        User(row.getString("userId"), email)
      }
    }
  }

  def getAllUsers(): Future[immutable.Seq[User]] = {
    session.selectAll(
      """
        |SELECT userId, email FROM userByEmail
      """.stripMargin
    ) map { rows =>
      rows.to[immutable.Seq] map { row =>
        User(row.getString("userId"), row.getString("email"))
      }
    }
  }

}

class UserEventProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[UserEvent]
{
  private var insertUserForEmailStatement: PreparedStatement = null

  override def buildHandler =
    readSide.builder[UserEvent]("userEventOffset")
      .setGlobalPrepare(createTables)
      .setPrepare(_ => prepareStatements())
      .setEventHandler[UserCreated](e => insertUser(e.event))
      .build

  override def aggregateTags =
    UserEvent.Tag.allTags

  private def insertUser(user: UserCreated) = {
    Future.successful(immutable.Seq(insertUserForEmailStatement.bind(user.userId, user.email)))
  }

  private def createTables() = {
    for {
      _ <- session.executeCreateTable(
        """
          |CREATE TABLE IF NOT EXISTS userByEmail (
          |  userId text,
          |  email text PRIMARY KEY
          |  )
        """.stripMargin)
    } yield Done
  }

  private def prepareStatements() = {
    for {
      insertUserForEmail <- session.prepare(
        """
          |INSERT INTO userByEmail(
          |  userId,
          |  email
          |) VALUES (?, ?)
        """.stripMargin)
    } yield {
      insertUserForEmailStatement = insertUserForEmail
      Done
    }
  }
}