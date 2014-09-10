package controllers

import play.api.mvc.{Action, Controller}
import models.ProductObj
import models.{Product,ProductsTable}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import play.api.mvc.Flash
import scala.slick.session.Session
import play.api.libs.json._
import play.api.db._
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
	
object Products extends Controller {
	//lazy val database = Database.forDataSource(DB.getDataSource())
    private val productForm: Form[Product] = Form(
		mapping(
				"ean" -> longNumber.verifying("validation.ean.duplicate", ProductsTable.findByEan(_).isEmpty),
				"name" -> nonEmptyText,
				"description" -> nonEmptyText
		)(Product.apply)(Product.unapply)
	)
 

	def list = Action { implicit request =>
 		val products = ProductsTable.findAll
 		/*val list = database withSession {
 			val bars = (for (b <- ProductsTable) yield b.clone).list 
 			bars
 		}*/
 		
		Ok (views.html.list(products))
	}
	def show(ean: Long) = Action { implicit request =>
    	ProductsTable.findByEan(ean).map { product =>
    		Ok(views.html.details(product))
    	}.getOrElse(NotFound)
	}
	
    def save = Action { implicit request =>
	    val newProductForm = productForm.bindFromRequest()
	
	    newProductForm.fold(
	      hasErrors = { form =>
	        Redirect(routes.Products.newProduct()).flashing(Flash(form.data) +("error" -> Messages("validation.errors")))
	      },
	      success = { newProduct =>
	        ProductsTable.insert(newProduct)
	        val message = Messages("products.new.success", newProduct.name)
	        Redirect(routes.Products.show(newProduct.ean)).flashing("success" -> message)
	      }
	    )
	}
    
    def newProduct = Action { implicit request =>
		val form = if (flash.get("error").isDefined)
			productForm.bind(flash.data)
		else
			productForm
		
		Ok(views.html.editProduct(form))
	}
}  