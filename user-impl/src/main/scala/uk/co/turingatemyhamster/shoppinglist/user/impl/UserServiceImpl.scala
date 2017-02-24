package uk.co.turingatemyhamster.shoppinglist.user.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import uk.co.turingatemyhamster.shoppinglist.user.api._

/**
  *
  *
  * @author Matthew Pocock
  */
class UserServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends UserService {

  override def createUser: ServiceCall[EmailAddress, User] = ServiceCall { emailAddress =>
    // this mints a new userId
    val userId = UUID.randomUUID().toString
    val ref = persistentEntityRegistry.refFor[UserEntity](userId)

    ref.ask(CreateUser(userId = userId, email = emailAddress.email))
  }

  override def getUser(userId: String): ServiceCall[NotUsed, User] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[UserEntity](userId)

    ref.ask(GetUser(userId))
  }

  override def addFriend(userId: String): ServiceCall[FriendId, NotUsed] = ServiceCall { friendId =>
    val ref = persistentEntityRegistry.refFor[UserEntity](userId)

    ref.ask(AddFriend(userId, friendId.id))
  }

  override def listFriends(userId: String): ServiceCall[NotUsed, UserFriends] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[UserEntity](userId)

    ref.ask(ListFriends(userId))
  }
}
