package uk.co.turingatemyhamster.shoppinglinst.webClient

import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.window

import scala.scalajs.js.timers._
import japgolly.scalajs.react._
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.HTMLFormElement
import uk.co.turingatemyhamster.shoppinglinst.webClient.services.{AjaxClient, UserClient}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
  * Created by nmrp3 on 03/03/17.
  */
@JSExport("LoginMain")
object LoginMain extends js.JSApp {

  println(s"protocol: ${window.location.protocol}")
  println(s"")
  val ajaxClient = AjaxClient.atBaseUrl(window.location.protocol + "//" + window.location.host)
  val userClient = new UserClient(ajaxClient)


  val formRef = Ref[HTMLFormElement]("loginForm")

  case class State(emailAddress: Option[String] = None,
                   userId: Option[String] = None)
  {
    def foundUser = userId.isDefined
  }


  class Backend($: BackendScope[Unit, State]) {
    def render(s: State) =
      <.div(
        <.div(
          <.input(
            ^.`type` := "text",
            ^.placeholder := "your@email",
            ^.name := "email",
            s.emailAddress map (ea => ^.value := ea),
            ^.onChange ==> handleEmailChanged _
          ),
          <.button(
            ^.onClick --> handleLogin,
            ^.disabled := !s.foundUser,
            "log in"
          )
        ),
        <.form(
          ^.action := "/",
          ^.method := "POST",
          ^.ref := formRef,
          <.input(
            ^.`type` := "hidden",
            ^.name := "userId",
            s.userId map (id =>  ^.value := id)
          )
        )
      )

    def handleLogin = Callback {
      formRef($).get.submit()
    }

    def handleEmailChanged(event: ReactEventI) = {
      val email = event.target.value
      println(s"Email changed to $email")
      val userF = userClient.queryByEmail(UserClient.EmailAddress(email)).map { u =>
        println(s"Retrieved user $u for email $email")
        $.modState(s => s.copy(userId = Some(u.userId))).runNow()
        println(s"Set user ID")
      }.recover {
        case t =>
          println(s"No user with email $email")
          t.printStackTrace
          println(s"Clearing user ID")
          $.modState(s => s.copy(userId = None)).runNow()
      }
      $.modState(s => s.copy(emailAddress = Some(email)))
    }
  }

  val LoginScreen = ReactComponentB[Unit]("login")
    .initialState(State())
    .renderBackend[Backend]
    .build

  @JSExport
  def main(): Unit = {
    val screen = LoginScreen()
    screen render dom.document.getElementById("root")
  }
}
