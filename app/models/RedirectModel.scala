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

case class RedirectModel(id: Option[Long], uid: String, var iurl: String, qargs: String,rurl :String)

object RedirectsTable extends Table[RedirectModel]("redirects") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def uid = column[String]("uid")
  def iurl = column[String]("iurl")
  def qargs = column[String]("qargs")
  def rurl = column[String]("rurl")
  def * = id.? ~ uid ~ iurl ~ qargs ~ rurl <> (RedirectModel, RedirectModel.unapply _)
  def forInsert = uid ~ iurl ~ qargs ~ rurl <> (
    t => RedirectModel(None, t._1, t._2, t._3,t._4),
    (p: RedirectModel) => Some((p.uid, p.iurl, p.qargs,p.rurl)))
  
   /**
   * Deletes a product.
   */
  def delete(id: Long) {
    withSession { implicit session =>
      RedirectsTable.where(_.id === id).delete
    }
  }

  def find(id: Long): Option[RedirectModel] = withSession { implicit session =>
    Query(RedirectsTable).filter(_.id === id).list.headOption
  }

  /**
   * Returns the product with the given EAN code.
   */
  def findByUid(uid: String): Option[RedirectModel] = withSession { implicit session =>
    Query(RedirectsTable).filter(_.uid === uid).list.headOption
  }
  
  def findByURL(iurl: String): Option[RedirectModel] = withSession { implicit session =>
    Query(RedirectsTable).filter(_.iurl === iurl).list.headOption
  }

  /**
   * Returns all products sorted by EAN code.
   */
  def findAll: List[RedirectModel] = withSession { implicit session =>
    Query(RedirectsTable).sortBy(_.uid).list
  }

  /**
   * Inserts the given product.
   */
  def insert(redirect: RedirectModel) {
    withSession { implicit session =>
      RedirectsTable.forInsert.insert(redirect)
    }
  }

  /**
   * Updates the given product.
   */
  def update(id: Long, redirect: RedirectModel) {
    withSession { implicit session =>
      RedirectsTable.where(_.id === id).update(redirect)
    }
  }
}