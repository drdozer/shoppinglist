/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers

import play.api.{Configuration, Environment}
import play.api.mvc._

class Application(implicit val config: Configuration, env: Environment) extends Controller {

  def redirectToLogin = Action {
    Redirect("/login")
  }

  def index = Action { implicit request =>
    request.body.asFormUrlEncoded.get.get("userId") match {
      case None => Redirect("/login")
      case Some(userIds) if userIds.length == 1 =>
        Ok(views.html.index.render(userIds.head, "ShoplistR", config, env)) // views generated code is merging parameter lists
    }
  }

  def signUp = Action {
    Ok(views.html.signUp.render("ShoplistR", config, env))
  }

  def login = Action {
    Ok(views.html.login.render("ShoplistR", config, env))
  }
}
