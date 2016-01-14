package controllers

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName
import play.api._
import play.api.mvc._

import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import models.{Binder, Tag, Bookmark}
import TagHelper._
//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Tags extends Controller {

  def list = Action { implicit request =>
    val tagsList = Tag.findAll
    val tags = mapList(tagsList)(_.name)
    Ok(views.html.tags.list(tags.toList))
  }

  /* corresponds to
   * dashboard > :username > tags
   */
  def listByUser(username: String) = Action { implicit request =>

    val bookmarkTags = Tag.findByUsername(username)
    val binders = Binder.findAll(username)
    val binderTags = for {
      binder <- binders
      tag <- binder.tags
    } yield(Tag(tag, binder.username, binder.alias, "binders/"+binder.name)) //TODO: this binder.name needs to be changed to binder url eventually

    val tags = mapList(bookmarkTags ::: binderTags)(_.name)
    Ok(views.html.tags.list(tags.toList))
  }
  /* corresponds to
   * dashboard > :username > tags > :tagName
   */
  def detailsUser(username: String, tagName: String) = Action { implicit request =>
    val bookmarkTags = Tag.findByNameAndUsername(username, tagName).distinct
    val binders = Binder.findAll(username)
    val binderTags = for {
      binder <- binders
      tag <- binder.tags
    } yield(Tag(tag, binder.username, binder.alias, binder.name)) //TODO: The second field, the key, should be SHA1(username+binder.name)

    val tags = bookmarkTags ::: binderTags.filter(_.name == tagName)
    Ok(views.html.tags.details(tagName, tags))
  }

  /***********************************************
    * The show details Action: retrieves details for a particular item
    ***********************************************/
  def details(tagName: String) = Action { implicit request =>
    val tags = Tag.findByName(tagName)
    Ok(views.html.tags.details(tagName, tags))
  }

}