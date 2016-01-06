package controllers

import play.api._
import play.api.mvc._

import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import models.Order
import models.Bookmark

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Bookmarks extends Controller {

  def list = Action { implicit request =>
    val bookmarks = Bookmark.findAll
    Ok(views.html.bookmarks.list(bookmarks))
  }

  /***********************************************
    * The show details Action: retrieves details for a particular item
    ***********************************************/
  def details(bookmarkName: String) = Action { implicit request =>
    Bookmark.findByName(bookmarkName).map { bookmark =>  //render a product barcode page
      Ok(views.html.bookmarks.details(bookmark))
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