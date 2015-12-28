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

import models.Product

class Products extends Controller {

  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying(
        "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

  def list = Action { implicit request =>
    val products = Product.findAll
    Ok(views.html.products.list(products))
  }

  def show(ean: Long) = Action { implicit request =>
    Product.findByEan(ean).map { product =>  //render a product barcode page
      Ok(views.html.products.details(product))
    }.getOrElse(NotFound)  //or return a 404 page
  }

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

  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()

    newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.Products.newProduct())
          .flashing(Flash(form.data) +
            ("error" -> Messages("validation errors")))
      },
      success = { newProduct =>
        Product.add(newProduct)
        val message = Messages("products.new.success", newProduct.name)
        Redirect(routes.Products.show(newProduct.ean))
          .flashing("success" -> message)
      }
    )
  }


}
