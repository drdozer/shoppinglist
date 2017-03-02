package uk.co.turingatemyhamster.shoppinglinst.webClient

import japgolly.scalajs.react.ReactDOM
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

  sealed trait Loc
  
  case object WelcomeLoc extends Loc
  
  
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    
    (staticRoute(root, WelcomeLoc) ~> ???
      ).notFound(redirectToPage(WelcomeLoc)(Redirect.Replace))
  }.renderWith(layout)
  
  
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
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
