/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers

import play.api.{Configuration, Environment}
import play.api.mvc._

class Application(implicit val config: Configuration, env: Environment) extends Controller {

  def index = Action {
    Ok(views.html.index.render("ShoplistR", config, env)) // views generated code is merging parameter lists
  }

}
