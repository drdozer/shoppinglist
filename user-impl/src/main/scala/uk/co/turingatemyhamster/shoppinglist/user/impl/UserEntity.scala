package uk.co.turingatemyhamster.shoppinglist.user.impl

import java.time.Instant

import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import play.api.libs.json.{Format, Json}
import scala.collection.immutable
import uk.co.turingatemyhamster.shoppinglist.user.api.{User, UserFriends}

/**
  *
  *
  * @author Matthew Pocock
  */
class UserEntity extends PersistentEntity {

  override type Command = UserCommand[_]
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = UserState(None)

  override def behavior: Behavior = {
    case UserState(maybeUserWithFriends, _) => Actions().onCommand[CreateUser, User] {
      case (CreateUser(userId, email), ctx, state) =>
        // todo: should look up users by email and fail if any already exists
        val newUser = User(userId = userId, email = email)
        ctx.thenPersist(
          UserCreated(newUser.userId, newUser.email)
        ) { _ =>
          ctx.reply(newUser)
        }
    }.onCommand[AddFriend, NotUsed] {
      case (AddFriend(userId, friendId), ctx, state) =>
        // should validate that friendId is a valid user ID
        ctx.thenPersist(
          FriendAdded(userId, friendId)
        ) { _ =>
          ctx.reply(NotUsed)
        }
    }.onReadOnlyCommand[GetUser, User] {
      case (GetUser(userId), ctx, UserState(None, _)) =>
        ctx.invalidCommand(s"No user for id: $userId")
      case (GetUser(userId), ctx, UserState(Some(withFriends), _)) =>
        ctx.reply(User(userId = withFriends.userId, email = withFriends.email))
    }.onReadOnlyCommand[ListFriends, UserFriends] {
      case (ListFriends(userId), ctx, UserState(None, _)) =>
        ctx.invalidCommand(s"No user for id: $userId")
      case (ListFriends(userId), ctx, UserState(Some(withFriends), _)) =>
        ctx.reply(UserFriends(userId = userId, friends = withFriends.friends))
    }.onEvent {
      case (UserCreated(userId, email, _), UserState(None, _)) =>
        UserState(Some(UserWithFriends(userId = userId, email = email, friends = immutable.Seq.empty)))
      case (FriendAdded(userId, friendId, _), UserState(Some(withFriends), _)) =>
        UserState(Some(withFriends.copy(friends = withFriends.friends :+ friendId)))
    }
  }
}


case class UserState(withFriends: Option[UserWithFriends], timestamp: Instant = Instant.now())

object UserState {
  implicit val format: Format[UserState] = Json.format
}


case class UserWithFriends(userId: String, email: String, friends: immutable.Seq[String])

object UserWithFriends {
  implicit val format: Format[UserWithFriends] = Json.format
}
