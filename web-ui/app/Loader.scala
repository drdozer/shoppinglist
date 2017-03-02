import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.api.{ServiceAcl, ServiceInfo}
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Mode}
import play.api.i18n.I18nComponents
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import com.softwaremill.macwire._
import controllers.Assets

import scala.collection.immutable
import scala.concurrent.ExecutionContext

abstract class WebGateway(context: Context) extends BuiltInComponentsFromContext(context)
                                                    with I18nComponents
                                                    with AhcWSComponents
                                                    with LagomServiceClientComponents
{
  override lazy val serviceInfo: ServiceInfo = ServiceInfo(
    "web-ui",
    Map(
      "web-ui" -> immutable.Seq(ServiceAcl.forPathRegex("(?!/api/).*"))
    )
  )

  override implicit lazy val executionContext: ExecutionContext = actorSystem.dispatcher

  protected implicit lazy val playConfig = context.initialConfiguration
  protected implicit lazy val playEnv = context.environment

  override lazy val router: Routes = {
    val prefix = "/"
    wire[Routes]
  }

  lazy val cApplication: controllers.Application = wire[controllers.Application]
  lazy val assets: Assets = wire[Assets]
}

class WebGatewayLoader extends ApplicationLoader {
  override def load(context: Context) = context.environment.mode match {
    case Mode.Dev =>
      new WebGateway(context) with LagomDevModeComponents {}.application
    case _ =>
      new WebGateway(context) {
        override def serviceLocator = NoServiceLocator
      }.application
  }
}