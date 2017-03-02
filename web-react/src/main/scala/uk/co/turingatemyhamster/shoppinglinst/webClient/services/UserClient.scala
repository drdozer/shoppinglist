package spatutorial.client.services

import upickle.default._

import scala.collection.immutable
import scala.concurrent.Future


/** A client to the User service.
  *
  *
  * @author Matthew Pocock
  */
class UserClient(ajaxClient: AjaxClient) {
  def createUser(emailAddress: UserClient.EmailAddress): Future[UserClient.User] =
    ajaxClient.post[UserClient.EmailAddress, UserClient.User]("/api/users", emailAddress)

  def getAllUsers: Future[UserClient.Users] =
    ajaxClient.get[UserClient.Users]("/api/users")

  def getUser(userId: String): Future[UserClient.User] =
    ajaxClient.get[UserClient.User]("/api/users/$userId")

  def queryByEmail(emailAddress: UserClient.EmailAddress): Future[UserClient.User] =
    ajaxClient.post[UserClient.EmailAddress, UserClient.User]("/api/users/byEmail", emailAddress)

  def addFriend(userId: String, friendId: UserClient.FriendId): Future[Unit] =
    ajaxClient.post[UserClient.FriendId, Unit]("/api/users/$userId", friendId)

  def listFriends(userId: String): Future[UserClient.UserFriends] =
    ajaxClient.get[UserClient.UserFriends]("/api/users/$userId/friends")
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
