package uk.co.turingatemyhamster.shoppinglist.user.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents
import uk.co.turingatemyhamster.shoppinglist.user.api.UserService
import com.softwaremill.macwire._
import play.api.Environment

import scala.concurrent.ExecutionContext


/**
  *
  *
  * @author Matthew Pocock
  */
class UserLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new UserApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new UserApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[UserService]
  )
}

trait UserComponents
  extends LagomServerComponents
          with CassandraPersistenceComponents
{
  implicit def executionContext: ExecutionContext
  def environment: Environment

  override lazy val lagomServer = LagomServer.forServices(
    bindService[UserService].to(wire[UserServiceImpl])
  )

  override lazy val jsonSerializerRegistry = UserSerializerRegistry

  lazy val userRepository = wire[UserRepository]

  persistentEntityRegistry.register(wire[UserEntity])

  readSide.register(wire[UserEventProcessor])
}

abstract class UserApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
          with UserComponents
          with AhcWSComponents
{
}