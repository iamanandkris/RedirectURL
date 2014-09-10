package models

import play.api.db.slick.Config.driver.simple._
import play.api.db._
import play.api.db.DB
import play.api.Play.current
import scala.slick.lifted.Query
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import play.api.mvc.Flash
import play.api.libs.json._
import play.api.GlobalSettings
import play.api.Application
import play.api.db.slick.DB.withSession

case class Product(ean: Long, name: String, description: String)

object ProductsTable extends Table[Product]("products"){
  def ean=column[Long]("ean",O.AutoInc,O.PrimaryKey)
  def name=column[String]("name")
  def description=column[String]("description") 
  def * = ean ~ name ~ description <> (Product, Product.unapply _)
  
  def forInsert = ean ~ name ~ description <> (
    t => Product(t._1, t._2, t._3),
    (p: Product) => Some((p.ean, p.name, p.description)))
    
  def find(ean: Long): Option[Product] = withSession{ implicit session =>
    Query(ProductsTable).filter(_.ean === ean).list.headOption
  }
 /**
   * Returns the product with the given EAN code.
   */
  def findByEan(ean: Long): Option[Product] = withSession { implicit session =>
    Query(ProductsTable).filter(_.ean === ean).list.headOption
  }

  /**
   * Returns all products sorted by EAN code.
   */
  def findAll: List[Product] = withSession { implicit session =>
    Query(ProductsTable).sortBy(_.ean).list
  }

  /**
   * Inserts the given product.
   */
  def insert(product: Product) {
    withSession { implicit session =>
      ProductsTable.forInsert.insert(product)
    }
  }
  
   /**
   * Updates the given product.
   */
  def update(id: Long, product: Product) {
    withSession { implicit session =>
      ProductsTable.where(_.ean === ean).update(product)
    }
  }
}