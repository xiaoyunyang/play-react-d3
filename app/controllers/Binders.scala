package controllers

import play.api._
import play.api.mvc._

import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import models.Order
import models.Binder
import models.User

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Binders extends Controller {

  def list(username: String) = Action { implicit request =>
    val binders = Binder.findAll(username)
    User.findByUsername(username).map { user =>
      Ok(views.html.binders.list(user,binders))
    }.getOrElse(NotFound)
  }

  /***********************************************
    * The details Action:
    * retrieves details for a particular binder
    ***********************************************/
  def details(username: String, name: String) = Action { implicit request =>
    Binder.findByName(name).map { binder =>
      Ok(views.html.binders.details(binder))
    }.getOrElse(NotFound)  //or return a 404 page
  }

  /**
    * Suspends an HTTP request while waiting for asynchronous processing.
    */
  def backlog(warehouse: String) = Action.async {
    import ExecutionContext.Implicits.global
    val backlog = Future {
      models.Order.backlog(warehouse)
    }
    backlog.map(value => Ok(value))
  }
}