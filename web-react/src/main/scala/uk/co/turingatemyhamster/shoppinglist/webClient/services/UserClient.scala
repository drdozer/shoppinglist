package uk.co.turingatemyhamster.shoppinglist.webClient.services

import upickle.default._

import scala.collection.immutable
import scala.concurrent.Future


/** A client to the User service.
  *
  *
  * @author Matthew Pocock
  */
class UserClient(ajaxClient: AjaxClient) {
  import UserClient._

  def createUser(emailAddress: EmailAddress): Future[User] =
    ajaxClient.post[UserClient.EmailAddress, UserClient.User]("/api/users", emailAddress)

  def getAllUsers: Future[Users] =
    ajaxClient.get[Users]("/api/users")

  def getUser(userId: String): Future[User] =
    ajaxClient.get[User]("/api/users/$userId")

  def queryByEmail(emailAddress: EmailAddress): Future[User] =
    ajaxClient.post[EmailAddress, User]("/api/users/byEmail", emailAddress)

  def addFriend(userId: String, friendId: FriendId): Future[Unit] =
    ajaxClient.post[FriendId, Unit]("/api/users/$userId", friendId)

  def listFriends(userId: String): Future[UserFriends] =
    ajaxClient.get[UserFriends]("/api/users/$userId/friends")
}

object UserClient {
  case class EmailAddress(email: String)

  object EmailAddress {
    implicit val format: ReadWriter[EmailAddress] = macroRW[EmailAddress]
  }


  case class User(userId: String, email: String)

  object User {
    implicit val format: ReadWriter[User] = macroRW[User]
  }


  case class Users(users: immutable.Seq[User])

  object Users {
    implicit val format: ReadWriter[Users] = macroRW[Users]
  }


  case class UserFriends(userId: String, friends: immutable.Seq[String])

  object UserFriends {
    implicit val format: ReadWriter[UserFriends] = macroRW[UserFriends]
  }


  case class FriendId(id: String)

  object FriendId {
    implicit val format: ReadWriter[FriendId] = macroRW[FriendId]
  }
}
