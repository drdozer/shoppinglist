package uk.co.turingatemyhamster.shoppinglist.shoppinglist.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

import uk.co.turingatemyhamster.shoppinglist.shoppinglist.api.ShoppingListService

/**
  *
  *
  * @author Matthew Pocock
  */
class ShoppingListLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new ShoppingListApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ShoppingListApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[ShoppingListService]
  )
}

abstract class ShoppingListApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
          with CassandraPersistenceComponents
          with AhcWSComponents
{

  override lazy val lagomServer = LagomServer.forServices(
    bindService[ShoppingListService].to(wire[ShoppingListServiceImpl])
  )

  override lazy val jsonSerializerRegistry = ShoppingListSerializerRegistry

  persistentEntityRegistry.register(wire[ShoppingListEntity])
}