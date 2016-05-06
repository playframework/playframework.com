package modules

import javax.inject.{Inject, Singleton}

import models.modules._
import play.api.db.Database
import services.modules.ModuleDao

@Singleton
class DbModuleDao @Inject() (db: Database) extends ModuleDao {

  import anorm.SqlParser._
  import anorm._

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

  def findEverything() = db.withConnection { implicit c =>
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