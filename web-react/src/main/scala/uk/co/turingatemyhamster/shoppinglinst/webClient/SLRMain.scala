package uk.co.turingatemyhamster.shoppinglinst.webClient

import diode.react.ModelProxy
import japgolly.scalajs.react.{ReactComponentB, ReactComponentU, ReactDOM, TopNode}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import uk.co.turingatemyhamster.shoppinglinst.webClient.components.GlobalStyles

/**
  *
  *
  * @author Matthew Pocock
  */
@JSExport("SLRMain")
object SLRMain extends js.JSApp {

  sealed trait LSPages

  case object Home extends LSPages

  case object Login extends LSPages

  case object Signup extends LSPages

  case object Shoppinglists extends LSPages

  case class ViewList(listId: String) extends LSPages


  val routerConfig = RouterConfigDsl[LSPages].buildConfig { dsl =>
    import dsl._

    (staticRoute(root, Home) ~> renderR(ctl => SLRCircuit.wrap(identity(_: UserStatus[Unit, Unit])) { proxy =>
      proxy.value match {
        case UserIsLoggedOut(state) =>
          HomeLoggedOut(ctl, proxy)
      }
    })
      ).notFound(redirectToPage(Home)(Redirect.Replace))
  }.renderWith(layout _)


  def layout(c: RouterCtl[LSPages], r: Resolution[LSPages]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial"))
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }
  
  @JSExport
  def main(): Unit = {
    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
  
}

object HomeLoggedOut {
  case class Props(router: RouterCtl[SLRMain.LSPages], proxy: ModelProxy[UserStatus[Unit, Unit]])

  private val component = ReactComponentB[Props]("Welcome")
    .render(p => <.div("Welcome to ShoppinglistR"))
    .build

  def apply(router: RouterCtl[SLRMain.LSPages], proxy: ModelProxy[UserStatus[Unit, Unit]]) =
    component(Props(router, proxy))
}