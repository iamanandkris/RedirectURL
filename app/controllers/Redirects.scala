package controllers

import play.api.mvc.{Action, Controller}
import models.ProductObj
import models.{RedirectModel,RedirectsTable}
import play.api.mvc._
import play.api.mvc.Flash
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import scala.slick.session.Session
import play.api.libs.json._
import play.api.db._
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import views.html.helper
import play.api._

object Redirects extends Controller {
 private val redirectForm: Form[RedirectModel] = Form(
		mapping(
				"id" -> optional(longNumber),
				"uid" -> nonEmptyText.verifying("validation.ean.duplicate", RedirectsTable.findByUid(_).isEmpty),
				"iurl" -> optional(text),
				"qargs" -> nonEmptyText,
				"rurl" -> nonEmptyText
		)(RedirectModel.apply)(RedirectModel.unapply)
	)
 

	def list = Action { implicit request =>
 		val products = RedirectsTable.findAll
 		helper.inputText(redirectForm("uid"))
		Ok (views.html.redirectList(products))
	}
	def show(uid: String) = Action { implicit request =>
    	RedirectsTable.findByUid(uid).map { product =>
    		Ok(views.html.redirectDetails(product))
    	}.getOrElse(NotFound)
	}
	
    def save = Action { implicit request =>
	    val newProductForm = redirectForm.bindFromRequest()
	
	    newProductForm.fold(
	      hasErrors = { form =>
	        Redirect(routes.Redirects.newProduct()).flashing(Flash(form.data) +("error" -> Messages("validation.errors")))
	      },
	      success = { newProduct =>
	        RedirectsTable.insert(newProduct)
	        val message = Messages("products.new.success", newProduct.uid)
	        Redirect(routes.Redirects.show(newProduct.uid)).flashing("success" -> message)
	      }
	    )
	}
    
    def newProduct = Action { implicit request =>
		val form = if (flash.get("error").isDefined)
			redirectForm.bind(flash.data)
		else
			redirectForm
		
		Ok(views.html.editRedirect(form))
	}
    
    def getString = Action { implicit request =>
		Ok("true")
	}
    
    def javascriptRoutes = Action { implicit request =>
		import routes.javascript._
		Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.Redirects.getString)).as("text/javascript")
	}
    
    def redirectLogic(redirectParam : String) = Action{
	  request =>
	  val test = request.uri
	  
	  println("URI = " + test)
	  println("Rest of the urls = " + redirectParam)
	  
	  val tempModel = RedirectsTable.findByURL(test)
	  val url = tempModel match {
	    case Some(s) => s.rurl
	    case None => "en.wikipedia.org/wiki/Sreevalsan_J_Menon"
	  }
	  
	  println("Return urls = " + url)
	  Redirect("http://"+url)
  }
}
 