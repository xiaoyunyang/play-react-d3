package controllers

//required by default
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

//required for json serialization and deserialization
import play.api.libs.json._
import play.api.libs.functional.syntax._

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

  // JSON serialization definition
  implicit val productWrites: Writes[Product] = (
    (JsPath \ "ean").write[Long] and
    (JsPath \ "name").write[String] and
    (JsPath \ "description").write[String]
  )(unlift(Product.unapply))

  // JSON deserialization definition
  /*
   * deserializes JSON into a JsSuccess that wraps an object of type T or a
   * JsError that gives you access to JSON parsing errors, following the pattern
   * of Scala’s Either[Error, T].
   */
  implicit val productReads: Reads[Product] = (
    (JsPath \ "ean").read[Long] and
    (JsPath \ "name").read[String] and
    (JsPath \ "description").read[String]
  )(Product.apply _)

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
  def saveJson(ean: Long) = Action(parse.json) { request =>
    val productJson = request.body
    val product = productJson.as[Product] //parse the JSON into a models.Product instance
    try {
      Product.save(product)
      Ok("Saved")
    }
    catch {
      case e:IllegalArgumentException =>
        BadRequest("Product not found")
    }
  }

}
