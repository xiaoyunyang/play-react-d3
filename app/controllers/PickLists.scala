package controllers

import play.api._
import play.api.mvc._
import models.PickList

import play.twirl.api.Html
import java.util.Date
import scala.concurrent.{ExecutionContext, Future}

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

/**
  * Created by xiaoyun on 1/2/16.
  */
class PickLists extends Controller {

  def preview(warehouse: String) = Action {
    val pickList = PickList.find(warehouse)
    val timestamp = new java.util.Date
    Ok(views.html.pickList(warehouse, pickList, timestamp))
  }


  //use Scala future to execute block of code asynchronously
  def sendAsync(warehouse: String) = Action {
    import ExecutionContext.Implicits.global
    Future {
      val pickList = PickList.find(warehouse)
      send(views.html.pickList(warehouse, pickList, new Date))
    }
    Redirect(routes.PickLists.index())
  }
  def index = Action {
    Ok(views.html.index())
  }
  /*
 * Renders the home page
 */
  /*
  def index = Action {
    Ok(views.html.index())
  }*/

  /**
    * Stub for ‘sending’ the pick list as an HTML document, e.g. by e-mail.
    */
  private def send(html: Html) {
    Logger.info(html.body)
    // Send pick list…
  }


}
