package uk.co.turingatemyhamster.shoppinglist.webClient

import diode.Circuit
import diode.react.ReactConnector

/**
  *
  *
  * @author Matthew Pocock
  */
object SLRCircuit extends Circuit[UserStatus[Unit, Unit]] with ReactConnector[UserStatus[Unit, Unit]] {

  override protected def initialModel = UserIsLoggedOut(())

  override protected val actionHandler = composeHandlers()

}
