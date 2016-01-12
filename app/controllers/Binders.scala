package controllers

import play.api._
import play.api.mvc._

import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}
import models.{Tag, Binder, User}

import helper._

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Binders extends Controller {

  def list(username: String) = Action { implicit request =>
    val binders = Binder.findAll(username)

    /* recommend Friends */
    val allTags = Tag.findAll //all tags associated with bookmarks

    val tags = Tag.findByUsername(username) //tags associated with user's bookmarks

    val binderTags = binders.flatMap(_.tags) //tags associated with binders

    val thruT = (tags.map(_.name) ::: binderTags)
      .flatMap(a => allTags.filter(_.name == a)).distinct

    val thruTnotP = thruT.filter(a => a.username != username)

    val pThruT = thruTnotP.foldRight(List[(String,String)]())((a,b) =>
      (a.username, a.name) :: b).distinct

    val bTags = binderTags.map(Tag(_, username, ""))

    //current implementation counts the fact that a tag are associated with a binder as +1
    //for the user
    val myTags = mapList(bTags ::: tags)(_.name).toList
      .foldRight(List[(String, Int)]())((a,b) => (a._1, a._2.size) :: b)

    User.findByUsername(username).map { user =>
      Ok(views.html.binders.list(user,binders, myTags, pThruT))
    }.getOrElse(NotFound)
  }

  def details(username: String, name: String) = Action { implicit request =>
    Binder.findByName(name).map { binder =>
      Ok(views.html.binders.details(username, binder))
    }.getOrElse(NotFound)  //or return a 404 page
  }

  def delete(username: String, bindername: String) = Action {
    Binder.delete(username, bindername)
    Redirect(routes.Binders.list(username)) //reverse routing
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