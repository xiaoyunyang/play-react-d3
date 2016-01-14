package controllers

import play.api._
import play.api.mvc._


import play.api.libs.json._
import TagHelper._
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}

import models.{Bookmark, Tag}

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Bookmarks extends Controller {

  /* Custom toJson methods for object representations */
  implicit object BookmarkWrites extends Writes[Bookmark] {
    def writes(b: Bookmark) = Json.obj(
      "key" -> JsString(b.key), //name is really the key
      "username" -> JsString(b.username),
      "title" -> JsString(b.name),
      "url" -> JsString(b.url),
      "favicon" -> JsString("https://stagr.in/img/placeholder.png"),
      "description" -> JsString(b.desc)
    )
  }

  def list = Action { implicit request =>
    val bookmarksList = Bookmark.findAll
    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findAll
    val tagToLinks = mapList(tags)(_.name)
    val linkToTags = mapList(tags)(_.url)
    Ok(views.html.bookmarks.list(bookmarks.toList, tagToLinks.toList, linkToTags.toList))
  }

  def listJson = Action { implicit request =>
    val bookmarksList = Bookmark.findAll

    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findAll
    val tagToLinks = mapList(tags)(_.name).toList
    val linkToTags = mapList(tags)(_.url).toList

    val items = Json.toJson(bookmarksList)

    val tagToItems =
      tagToLinks.map { a =>
        val names = a._2.map(a => a.url)
        Json.obj(
          "tag" -> JsString(a._1),
          "items" -> names //name is really the key
        )
      }
    val itemToTags =
      linkToTags.map { a =>
        val tags = a._2.map(a => a.name)
        Json.obj(
          "item" -> JsString(a._1),
          "tags" -> tags //name is really the key
        )
      }
    //val data = Json.arr(items,tagToItems,itemToTags)
    val data = Bookmark.findAll.map(_.key)
    Ok(Json.toJson(data))
  }

  def listByUser(username: String) = Action { implicit request =>
    val bookmarksList = Bookmark.findByUserName(username)
    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findByUsername(username)
    val tagToLinks = mapList(tags)(_.name)
    val linkToTags = mapList(tags)(_.url)

    Ok(views.html.bookmarks.list(bookmarks.toList, tagToLinks.toList, linkToTags.toList))
  }

  /*********************************************************************
    * The details Action:
    * retrieves details for a particular item
    *********************************************************************/
  // Future Expansion:
  // retrieves recommendation for a particular item based on shared tags
  def details(bookmarkName: String) = Action { implicit request =>
    Bookmark.findByName(bookmarkName).map { bookmark =>
      Ok(views.html.bookmarks.details(bookmark))
    }.getOrElse(NotFound)  //or return a 404 page
  }

  def detailsJson(key: String) = Action {
    Bookmark.findByKey(key).map { bookmark =>
      Ok(Json.toJson(bookmark))
    }.getOrElse(NotFound)
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