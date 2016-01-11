package controllers

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName
import play.api._
import play.api.mvc._

import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import models.Tag
import models.Bookmark
import helper._
//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Tags extends Controller {

  def list = Action { implicit request =>
    val tagsList = Tag.findAll
    val tags = mapList(tagsList)(_.name)
    Ok(views.html.tags.list(tags.toList))
  }
  def listByUser(username: String) = Action { implicit request =>
    val tagsList = Tag.findByUsername(username)
    val tags = mapList(tagsList)(_.name)
    Ok(views.html.tags.list(tags.toList))
  }
  //helper
  /*
  def mapList(tagsSet: List[Tag]): Map[String, List[Tag]] = {
    var tags = Map.empty[String, List[Tag]]
    def addTag(key: String, value: Tag) =
      tags += (key -> (value :: (tags get key getOrElse Nil)))
    tagsSet.foreach(a => addTag(a.name, a))
    tags
  }*/

  /***********************************************
    * The show details Action: retrieves details for a particular item
    ***********************************************/
  def details(tagName: String) = Action { implicit request =>
    val tags = Tag.findByName(tagName)
    Ok(views.html.tags.details(tagName, tags))
  }

}