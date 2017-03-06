/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers

import play.api.{Configuration, Environment}
import play.api.mvc._
import uk.co.turingatemyhamster.shoppinglist.user.api.UserService

import scala.concurrent.{ExecutionContext, Future}

class Application(userService: UserService
                 )(implicit val config: Configuration,
                   env: Environment,
                   executionContext: ExecutionContext)
  extends Controller
{

  def redirectToLogin = Action {
    Redirect("/login")
  }

  def index = Action.async { implicit request =>
    request.body.asFormUrlEncoded.get.get("userId") match {
      case None =>
        println("Attempted to view main page with no userId")
        Future.successful(Redirect("/login"))
      case Some(userIds) if userIds.length == 1 =>
        val userId = userIds.head
        println(s"Viewing main page with one userId `$userId`")
        userService.getUser(userId).invoke().map { u =>
          println(s"Fetched user `$u`. Loading main page.")
          Ok(views.html.index.render(u.userId, "ShoplistR", config, env))
        }.recoverWith {
          case t =>
            println(s"No user found for `$userId`. Caught: $t")
            Future.successful(Redirect("/login"))
        }
         // views generated code is merging parameter lists
      case Some(userIds) =>
        println("Attempting to view main page with multiple userIDs")
        Future.successful(Redirect("/login"))
    }
  }

  def signUp = Action {
    Ok(views.html.signUp.render("ShoplistR", config, env))
  }

  def login = Action {
    Ok(views.html.login.render("ShoplistR", config, env))
  }
}
