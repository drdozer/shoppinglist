package uk.co.turingatemyhamster.shoppinglinst.webClient

import uk.co.turingatemyhamster.shoppinglinst.webClient.services.UserClient

// The user is either logged out or in.
// If it is out, we have associated state of type `LoggedOut`.
// If it is in, we have associated state of type `LoggedIn`.
sealed trait UserStatus[+LoggedOut, +LoggedIn]

case class UserIsLoggedOut[+LoggedOut](state: LoggedOut) extends UserStatus[Nothing, LoggedOut]
case class UserIsLoggedIn[+LoggedIn](user: UserClient.User, state: LoggedIn) extends UserStatus[LoggedIn, Nothing]


