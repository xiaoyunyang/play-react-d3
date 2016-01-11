package controllers

//required by default
import play.api._
import play.api.mvc._

//required for form helpers
import play.api.data._
import play.api.data.Forms._

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import java.util.UUID

import play.api.libs.json.{JsArray, Json}
import play.api.mvc._

//required to be able to import model definitions
import models._

class Application extends Controller {

  def index = Action { implicit request =>
    //Redirect(routes.Products.list())
    Redirect(routes.Application.dashboard)
  }
  def react = Action {
    Ok(views.html.react())
  }

  def dashboard = Action { implicit request =>
    Ok(views.html.dashboard.list())
  }

  // The json keys. The 'id' field was added as without it we would get a warning like this:
  // Warning: Each child in an array or iterator should have a unique "key" prop. Check the render method of CommentList. See https://fb.me/react-warning-keys for more information.
  val JSON_KEY_COMMENTS = "comments"
  val JSON_KEY_AUTHOR = "author"
  val JSON_KEY_TEXT = "text"
  val JSON_KEY_ID = "id"


  // Initialise the comments list
  var commentsJson: JsArray = Json.arr(
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Pete Hunt", JSON_KEY_TEXT -> "This is one comment"),
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Jordan Walke", JSON_KEY_TEXT -> "This is *another* comment")
  )

  // Returns the comments list
  def comments = Action {
    Ok(commentsJson)
  }

  // Adds a new comment to the list and returns it
  def comment(author: String, text: String) = Action {
    val newComment = Json.obj(
      JSON_KEY_ID -> UUID.randomUUID().toString,
      JSON_KEY_AUTHOR -> author,
      JSON_KEY_TEXT -> text)
    commentsJson = commentsJson :+ newComment
    Ok(newComment)
  }




}