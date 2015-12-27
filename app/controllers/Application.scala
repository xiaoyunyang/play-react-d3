package controllers

//required by default
import play.api._
import play.api.mvc._

//required for form helpers
import play.api.data._
import play.api.data.Forms._

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

//required to be able to import model definitions
import models._

class Application extends Controller {
  //Form helper with validation to require non-empty field
  val taskForm = Form(
    "label" -> nonEmptyText
  )
  //An Action must return a Result that represents
  //the HTTP response to send back to the web browser
  def index(name: String) = Action {
    Redirect(routes.Application.tasks(name))
  }
  def tasks(name: String) = Action {
    Ok(views.html.index(name, Task.all(), taskForm))
  }

  def newTask = TODO

  def deleteTask(id: Long) = TODO

  def hello(name: String) = Action {
    //Ok("Hello "+ name)
    Ok(views.html.hello(name))
  }

}
