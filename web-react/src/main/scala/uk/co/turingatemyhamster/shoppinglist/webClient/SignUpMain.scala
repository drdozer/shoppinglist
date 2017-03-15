package uk.co.turingatemyhamster.shoppinglist.webClient

import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLFormElement
import org.scalajs.dom.window
import LoginMain.userClient
import uk.co.turingatemyhamster.shoppinglinst.webClient.services.UserClient
import uk.co.turingatemyhamster.shoppinglist.webClient.services.{AjaxClient, UserClient}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
  * Created by nmrp3 on 03/03/17.
  */
@JSExport("SignUpMain")
object SignUpMain extends js.JSApp {

  val ajaxClient = AjaxClient.atBaseUrl(window.location.protocol + "//" + window.location.host)
  val userClient = new UserClient(ajaxClient)


  case class State(emailAddress: Option[String] = None, userId: Option[String] = None, emailIsAvailable: Boolean = false)

  class Backend($: BackendScope[Unit, State]) {
    def render(s: State) =
      <.div(
        <.div(
          <.input(
            ^.`type` := "text",
            ^.placeholder := "your@email",
            ^.name := "email",
            ^.value := s.emailAddress,
            ^.onChange ==> handleEmailChanged _
          ),
          <.button(
            ^.onClick --> handleSignup,
            ^.disabled := !s.emailIsAvailable,
            "Sign up"
          )
        ),
        <.form(
          ^.action := "/",
          ^.method := "POST",
          <.input(
            ^.`type` := "hidden",
            ^.name := "userId",
            ^.value := s.userId
          )
        )
      )


    def handleSignup = $.state.map { s =>
      println("Handling signup attempt")
      s.emailAddress.map { e =>
        val uF = userClient.createUser(UserClient.EmailAddress(e))
        uF.onSuccess { case u => dom.window.location.assign("/login") }
        uF.onFailure { case t => }
        uF
      }
      ()
    }

    def handleEmailChanged(event: ReactEventI) = {
      val email = event.target.value
      println(s"Email changed to $email")
      val userF = userClient.queryByEmail(UserClient.EmailAddress(email))
      userF.onSuccess {
        case u =>
          println(s"Retrieved user $u for email $email")
          $.modState(_.copy(emailIsAvailable = false)).runNow()
      }
      userF.onFailure {
        case t =>
          println(s"No user with email $email")
          $.modState(_.copy(emailIsAvailable = true)).runNow()
      }
      $.modState(_.copy(emailAddress = Some(email)))
    }
  }

  val LoginScreen = ReactComponentB[Unit]("login")
    .initialState(State(None, None))
    .renderBackend[Backend]
    .build

  @JSExport
  def main(): Unit = {
    val screen = LoginScreen()
    screen render dom.document.getElementById("root")
  }
}
