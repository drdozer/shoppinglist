package uk.co.turingatemyhamster.shoppinglist.user.impl



import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}
import uk.co.turingatemyhamster.shoppinglist.user.api.{User, UserFriends}


sealed trait UserCommand[R] extends ReplyType[R]


case class CreateUser(userId: String, email: String) extends UserCommand[User]

object CreateUser {
  implicit val format: Format[CreateUser] = Json.format
}


case class GetUser(userId: String) extends UserCommand[User]

object GetUser {
  implicit val format: Format[GetUser] = Json.format
}


case class AddFriend(userId: String, friendId: String) extends UserCommand[NotUsed]

object AddFriend {
  implicit val format: Format[AddFriend] = Json.format
}


case class ListFriends(userId: String) extends UserCommand[UserFriends]

object ListFriends {
  implicit val format: Format[ListFriends] = Json.format
}
