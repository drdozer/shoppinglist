package uk.co.turingatemyhamster.shoppinglist.user.impl

import java.net.ProtocolException
import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{Forbidden, NotFound, TransportErrorCode, TransportException}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import uk.co.turingatemyhamster.shoppinglist.user.api._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  *
  * @author Matthew Pocock
  */
class UserServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                      userRepository: UserRepository)(
  implicit ec: ExecutionContext) extends UserService
{

  override def createUser: ServiceCall[EmailAddress, User] = ServiceCall { emailAddress =>
    for {
      usersWithMatchingEmail <- userRepository.getUsersForEmail(emailAddress.email)
      failIfMatchingUsers <- if(usersWithMatchingEmail.isEmpty)
        Future.successful(())
      else Future.failed(Forbidden(s"You can not create a user for ${emailAddress.email}"))
      userId = UUID.randomUUID().toString
      ref = persistentEntityRegistry.refFor[UserEntity](userId)
      newUser <- ref.ask(CreateUser(userId = userId, email = emailAddress.email))
    } yield newUser
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


  override def getAllUsers: ServiceCall[NotUsed, Users] = ServiceCall { _ =>
    userRepository.getAllUsers() map Users.apply
  }

  override def queryByEmail: ServiceCall[EmailAddress, User] = ServiceCall { emailAddress =>
    userRepository.getUsersForEmail(emailAddress.email) map { users =>
      users.headOption match {
        case None => throw NotFound(s"User with email ${emailAddress.email} does not exist")
        case Some(u) => u
      }
    }
  }
}
