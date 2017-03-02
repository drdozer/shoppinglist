package uk.co.turingatemyhamster.shoppinglist.user.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api._
import play.api.libs.json.{Format, Json}

import scala.collection.immutable


/**
  *
  *
  * @author Matthew Pocock
  */
trait UserService extends Service {

  def createUser: ServiceCall[EmailAddress, User]

  def getUser(userId: String): ServiceCall[NotUsed, User]

  def getAllUsers: ServiceCall[NotUsed, Users]

  def queryByEmail: ServiceCall[EmailAddress, User]

  def addFriend(userId: String): ServiceCall[FriendId, NotUsed]

  def listFriends(userId: String): ServiceCall[NotUsed, UserFriends]

  override final def descriptor = {
    import Service._

    named("user").withCalls(
      pathCall("/api/users", createUser),
      pathCall("/api/users", getAllUsers),
      pathCall("/api/users/:userId", getUser _),
      pathCall("/api/users/byEmail", queryByEmail),
      pathCall("/api/users/:userId/friends", addFriend _),
      pathCall("/api/users/:userId/friends", listFriends _)
    ).withAutoAcl(true)
  }
}


case class EmailAddress(email: String)

object EmailAddress {
  implicit val format: Format[EmailAddress] = Json.format
}


case class User(userId: String, email: String)

object User {
  implicit val format: Format[User] = Json.format
}


case class Users(users: immutable.Seq[User])

object Users {
  implicit val format: Format[Users] = Json.format
}


case class UserFriends(userId: String, friends: immutable.Seq[String])

object UserFriends {
  implicit val format: Format[UserFriends] = Json.format
}


case class FriendId(id: String)

object FriendId {
  implicit val format: Format[FriendId] = Json.format
}