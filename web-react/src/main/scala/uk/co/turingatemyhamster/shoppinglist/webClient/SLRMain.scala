package uk.co.turingatemyhamster.shoppinglist.webClient

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
import uk.co.turingatemyhamster.shoppinglinst.webClient.services.ShoppingListClient
import uk.co.turingatemyhamster.shoppinglist.shoppinglist.api.ShoppingListService
import uk.co.turingatemyhamster.shoppinglist.webClient.components.GlobalStyles
import uk.co.turingatemyhamster.shoppinglist.webClient.services.ShoppingListClient

trait ShoplistrLayout[Page] {
  def apply(c: RouterCtl[Page], r: Resolution[Page]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "ShoplistR"))
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }
}

/**
  *
  *
  * @author Matthew Pocock
  */
@JSExport("SLRMain")
object SLRMain {

  sealed trait LSPages

  case object AllListsPage extends LSPages
  case class ListPage(listId: String) extends LSPages

  val routerConfig = RouterConfigDsl[LSPages].buildConfig { dsl =>
    import dsl._

    (
      staticRoute(root, AllListsPage) ~> renderR(ctl => SLRCircuit.wrap(m => m)(proxy => AllLists(ctl, proxy)))
      ).notFound(redirectToPage(AllListsPage)(Redirect.Replace))
  }.renderWith(layout.apply _)

  object layout extends ShoplistrLayout[LSPages]

  @JSExport
  def main(userId: String): Unit = {
    println(s"Loaded page as $userId")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }

}

object AllLists {
  case class Props(router: RouterCtl[SLRMain.LSPages], proxy: ModelProxy[ShoppingListClient.ShoppingLists])

  private val component = ReactComponentB[Props]("Welcome")
    .render(p => <.div(
      <.div("Welcome to ShoppinglistR") /*,
      SLRCircuit.zoomMap(isLoggedOut[Unit, Unit] _)(LoginScreen.apply _)*/
    ))
    .build

  def apply(router: RouterCtl[SLRMain.LSPages], proxy: ModelProxy[UserStatus[Unit, Unit]]) =
    component(Props(router, proxy))
}