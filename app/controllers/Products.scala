package controllers

//required by default
import play.api._
import play.api.mvc._
import play.api.mvc.{Flash, Action, Controller}

//required for implicit to work
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

//required for Forms
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages

//models
import models.Product
//required for json
import play.api.libs.json._

class Products extends Controller {
  /*
   * productForm: dependency for the newProduct and save function
   */
  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying(
        "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

  implicit object ProductWrites extends Writes[Product] {
    def writes(p: Product) = Json.obj(
      "ean" -> Json.toJson(p.ean),
      "name" -> Json.toJson(p.name),
      "description" -> Json.toJson(p.description)
    )
  }

  /***********************************************
   * The list Action: retrieves a list of all items
   ***********************************************/
  def list = Action { implicit request =>
    val products = Product.findAll
    Ok(views.html.products.list(products))
  }

  def listJson = Action {
    val productCodes = Product.findAll.map(_.ean)
    Ok(Json.toJson(productCodes))
  }
  /***********************************************
   * The details Action: retrieves details for a particular item
   ***********************************************/
  def details(ean: Long) = Action { implicit request =>
    Product.findByEan(ean).map { product =>  //render a product barcode page
      Ok(views.html.products.details(product))
    }.getOrElse(NotFound)  //or return a 404 page
  }

  // gets an Option[Product] from the model and returns a response with
  // the product in JSON format, or a NotFound error response if there’s no
  // such product.
  def detailsJson(ean: Long) = Action {
    Product.findByEan(ean).map { product =>
      Ok(Json.toJson(product))
    }.getOrElse(NotFound)
  }

  def delete(ean: Long) = Action {
    Product.delete(ean)
    Redirect(routes.Products.list()) //reverse routing
  }

  /***********************************************
   * The new Action: use form information to create new item
   ***********************************************/
  def newProduct = Action { implicit request =>
    /*
    val form = if (flash.get("error").isDefined)
      productForm.bind(flash.data)
    else
      productForm
    */

    val form = productForm
    Ok(views.html.products.editProduct(form))
  }

  /***********************************************
   * Save the form for create new item
   ***********************************************/
  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()

    newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.Products.newProduct())
          .flashing(Flash(form.data) +
            ("error" -> Messages("validation.errors")))
      },
      success = { newProduct =>
        Product.add(newProduct)
        val message = Messages("products.new.success", newProduct.name)
        Redirect(routes.Products.details(newProduct.ean))
          .flashing("success" -> message)
      }
    )
  }

}
