package controllers

import play.api._
import play.api.mvc._

import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import models.Order

class Dashboard extends Controller {

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