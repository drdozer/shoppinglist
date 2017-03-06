package uk.co.turingatemyhamster.shoppinglinst.webClient

import scalajs.concurrent.JSExecutionContext.Implicits.runNow

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import org.scalajs.dom.window
import uk.co.turingatemyhamster.shoppinglinst.webClient.services.{AjaxClient, UserClient}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
  * Created by nmrp3 on 03/03/17.
  */
@JSExport("SignUpMain")
object SignUpMain extends js.JSApp {

  val ajaxClient = AjaxClient.atBaseUrl(window.location.protocol + "//" + window.location.host)
  val userClient = new UserClient(ajaxClient)


  case class State(emailAddress: Option[String], userId: Option[String])

  def handleLogin = CallbackTo(println("Handling login attempt"))
  def handleEmailChanged(event: ReactEventI) = CallbackTo {
    val email = event.target.value
    println(s"Email changed to $email")
    val userF = userClient.queryByEmail(UserClient.EmailAddress(email))
    userF.onSuccess {
      case u =>
        println(s"Retrieved user $u for email $email")
    }
    userF.onFailure {
      case t =>
        println(s"Unhappy with email $email")
    }
  }

  val LoginScreen = ReactComponentB[Nil.type]("login")
    .render(p =>
      <.div(
        <.div(
          <.input(
            ^.`type` := "text",
            ^.placeholder := "your@email",
            ^.name := "email",
            ^.onChange ==> handleEmailChanged _
          ),
          <.button(
            ^.onClick --> handleLogin,
            "log in"
          )
        ),
        <.form(
          ^.action := "/",
          ^.method := "POST",
          <.input(
            ^.`type` := "hidden",
            ^.name := "userId",
            ^.value := ""
          )
        )
      )
    )
    .build

  @JSExport
  def main(): Unit = {
    val screen = LoginScreen(Nil)
    screen render dom.document.getElementById("root")
  }
}
