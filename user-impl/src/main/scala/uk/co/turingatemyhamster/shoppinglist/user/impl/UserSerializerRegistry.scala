package uk.co.turingatemyhamster.shoppinglist.user.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import uk.co.turingatemyhamster.shoppinglist.user.api.{EmailAddress, FriendId, User, UserFriends}

import scala.collection.immutable

/**
  *
  *
  * @author Matthew Pocock
  */
object UserSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer[UserState],
    JsonSerializer[UserWithFriends],
    JsonSerializer[EmailAddress],
    JsonSerializer[User],
    JsonSerializer[UserFriends],
    JsonSerializer[FriendId],
    JsonSerializer[UserCreated],
    JsonSerializer[FriendAdded],
    JsonSerializer[CreateUser],
    JsonSerializer[GetUser],
    JsonSerializer[AddFriend],
    JsonSerializer[ListFriends]
  )
}
