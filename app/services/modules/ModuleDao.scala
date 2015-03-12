package services.modules

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import models.modules._
import play.api.db.Database

/**
 * DAO for accessing modules
 */
@ImplementedBy(classOf[DbModuleDao])
trait ModuleDao {
  /**
   * Find all the modules and all releases of the modules
   */
  def findEverything: Seq[(Module, Seq[Release])]

  /**
   * Find all modules that have a name like the given keyword
   */
  def findAll(keyword: String = ""): Seq[Module]

  /**
   * Find the given module and all its releases
   */
  def findById(name: String): Option[(Module, Seq[Release])]
}

@Singleton
class DbModuleDao @Inject() (db: Database) extends ModuleDao {

  import anorm._
  import anorm.SqlParser._

  private val moduleParser = {
    get[String]("Module.name") ~
      get[String]("Module.fullname") ~
      get[String]("Module.author") ~
      get[String]("Module.authorId") ~
      get[String]("Module.description") ~
      get[String]("Module.homepage") map
      flatten map
      (Module.apply _).tupled
  }

  private val releaseParser = {
    get[String]("ModuleRelease.version") ~
      get[java.util.Date]("ModuleRelease.publishedDate") ~
      get[String]("ModuleRelease.frameworkMatch") ~
      get[Boolean]("ModuleRelease.isDefault") map
      flatten map
      (Release.apply _).tupled
  }

  def findEverything = db.withConnection { implicit c =>
    SQL("""
        select * from Module
        join ModuleRelease on Module.id = ModuleRelease.module_id
        """).as(moduleParser ~ releaseParser *).groupBy(_._1).mapValues(_.map(_._2)).toSeq
  }

  def findAll(keyword: String = "") = db.withConnection { implicit c =>
    SQL("select * from Module where fullname like {keyword} order by name").on('keyword -> ("%" + keyword + "%")).as(moduleParser *)
  }

  def findById(name: String) = db.withConnection { implicit c =>
    val result = SQL("""
      select * from Module
      left join ModuleRelease on Module.id = ModuleRelease.module_id
      where name = {name}
                     """).on('name -> name).as( moduleParser ~ (releaseParser?) *)

    result.headOption.map {
      case module ~ _ => (module, result.flatMap(_._2).sortBy(_.date).reverse)
    }
  }
}