package controllers

import play.api._
import play.api.mvc._

import helper._
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}

//models
import models.Bookmark
import models.Tag

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Bookmarks extends Controller {

  def list = Action { implicit request =>
    val bookmarksList = Bookmark.findAll
    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findAll
    val tagToLinks = mapList(tags)(_.name)
    val linkToTags = mapList(tags)(_.url)
    Ok(views.html.bookmarks.list(bookmarks.toList, tagToLinks.toList, linkToTags.toList))
  }
  def listByUser(username: String) = Action { implicit request =>
    val bookmarksList = Bookmark.findByUserName(username)
    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findByUsername(username)
    val tagToLinks = mapList(tags)(_.name)
    val linkToTags = mapList(tags)(_.url)

    Ok(views.html.bookmarks.list(bookmarks.toList, tagToLinks.toList, linkToTags.toList))
  }

  /* details
   * retrieves details for a particular item
   * retrieves recommendation for a particular item based on shared tags
   */
  def details(bookmarkName: String) = Action { implicit request =>
    Bookmark.findByName(bookmarkName).map { bookmark =>
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