package controllers

import TagHelper._

import play.api.mvc.{Flash, Action, Controller}
import scala.concurrent.{ExecutionContext, Future}

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

//required for Forms
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

//models
import models.{Bookmark, Tag}


//required for json serialization and deserialization
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._ //combinator syntax

import scala.util.hashing.MurmurHash3._

class Bookmarks extends Controller {

  /*
   * Custom toJson methods for object representations
   * Format is combined JSON serializer (Writes) and deserializer (Reads)
   */
  implicit val bookmarkFormat = (
    (JsPath \ "key").format[Int] and
    (JsPath \ "url").format[String] and
    (JsPath \ "username").format[String] and
    (JsPath \ "title").format[String] and
    (JsPath \ "description").formatNullable[String] and
    (JsPath \ "favicon").format[String]
  )(Bookmark.apply, unlift(Bookmark.unapply))


  private val bookmarkForm  = Form(
    mapping(
      "url" -> nonEmptyText,
      "username" -> nonEmptyText,
      "title" -> nonEmptyText,
      "description" -> optional(nonEmptyText),
      "favicon" -> nonEmptyText
    )
    ((url, username, title, description, favicon) =>
          Bookmark(stringHash(""+url+username), url, username, title, description, favicon))
    ((bookmark: Bookmark) =>
          Some(bookmark.url, bookmark.username, bookmark.title, bookmark.description, bookmark.favicon))
  )

  /*********************************************************************
   * Create Actions:
   * Create new Bookmarks
   *********************************************************************/
  def createForm(username: String) = Action { implicit request =>
    Ok(views.html.bookmarks.form(bookmarkForm, username))
  }

  def create(username: String) = Action { implicit request =>
    bookmarkForm.bindFromRequest.fold(
      formWithErrors => {
        Ok(views.html.bookmarks.form(formWithErrors, username))
      },
      newBookmark => {
        Bookmark.add(newBookmark)
        val message = Messages("products.new.success", newBookmark.title)
        Redirect(routes.Bookmarks.details(username, newBookmark.title))
          .flashing("success" -> message)
      }
    )
  }
  def save(username: String) = Action { implicit request =>
    val newBookmarkForm = bookmarkForm.bindFromRequest()

    newBookmarkForm.fold(
      hasErrors = { form =>
        Redirect(routes.Bookmarks.createForm(username))
          .flashing(Flash(form.data) +
            ("error" -> Messages("validation.errors")))
      },
      success = { newBookmark =>
        Bookmark.add(newBookmark)
        val message = Messages("products.new.success", newBookmark.title)
        Redirect(routes.Bookmarks.details(username, newBookmark.title))
          .flashing("success" -> message)
      }
    )
  }


  /*********************************************************************
   * List Actions:
   * List Bookmarks with filters and format spec
   *********************************************************************/
  // TODO: write pattern matching / polymorphic functions to generalize list functions

  def list = Action { implicit request =>
    val bookmarksList = Bookmark.findAll
    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findAll
    val tagToLinks = mapList(tags)(_.name)
    val linkToTags = mapList(tags)(_.url)
    Ok(views.html.bookmarks.list("public", bookmarks.toList, tagToLinks.toList, linkToTags.toList))
  }

  def listJson = Action { implicit request =>
    val data = Bookmark.findAll.map(_.key)
    Ok(Json.toJson(data))
  }

  def listJsonReact = Action { implicit request =>
    val bookmarksList = Bookmark.findAll

    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findAll
    val tagToLinks = mapList(tags)(_.name).toList
    val linkToTags = mapList(tags)(_.key).toList

    val items = Json.toJson(bookmarksList)

    val tagToItems =
      tagToLinks.map { a =>
        val keys = a._2.map(a => a.key)
        Json.obj(
          "tag" -> JsString(a._1),
          "keys" -> keys //name is really the key
        )
      }
    val itemToTags =
      linkToTags.map { a =>
        val tags = a._2.map(a => a.name)
        Json.obj(
          "key" -> JsNumber(a._1),
          "tags" -> tags //name is really the key
        )
      }
    val data = Json.obj(
      "items" -> items,
      "tagToItems" -> tagToItems,
      "itemToTags" -> itemToTags
    )
    //val data = Bookmark.findAll
    Ok(Json.toJson(data))
  }

  def listByUser(username: String) = Action { implicit request =>
    val bookmarksList = Bookmark.findByUserName(username)
    val bookmarks = mapList(bookmarksList)(_.url)
    val tags = Tag.findByUsername(username)
    val tagToLinks = mapList(tags)(_.name)
    val linkToTags = mapList(tags)(_.url)

    Ok(views.html.bookmarks.list(username, bookmarks.toList, tagToLinks.toList, linkToTags.toList))
  }

  /*********************************************************************
  * The details Action:
  * retrieves details for a particular item
  *********************************************************************/
  // TODO: retrieves recommendation for a particular item based on shared tags
  def details(username: String, title: String) = Action { implicit request =>
    Bookmark.findByTitle(title).map { bookmark =>
      Ok(views.html.bookmarks.details(bookmark))
    }.getOrElse(NotFound)  //or return a 404 page
  }

  def detailsJson(key: Int) = Action {
    Bookmark.findByKey(key).map { bookmark =>
      Ok(Json.toJson(bookmark))
    }.getOrElse(NotFound)
  }

  def saveJson(key: Int) = Action(parse.json) { request =>
    val bookmarkJson = request.body
    val bookmark = bookmarkJson.as[Bookmark] //parse the JSON into a models.Bookmark instance
    try {
      Bookmark.save(bookmark)
      Ok("Saved")
    }
    catch {
      case e:IllegalArgumentException =>
        BadRequest("Bookmark not found")
    }
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