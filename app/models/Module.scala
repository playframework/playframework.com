package models

case class Module(name: String, fullname: String, author: String, authorId: String, description: String, homePage: String)

case class Release(version: String, date: java.util.Date, frameworkMatch: String, isDefault: Boolean)

object Module {
  
  import play.api._
  import play.api.db._
  
  import anorm._
  import anorm.SqlParser._
  
  import Play.current
  
  val parser = {
    get[String]("Module.name") ~
    get[String]("Module.fullname") ~
    get[String]("Module.author") ~
    get[String]("Module.authorId") ~
    get[String]("Module.description") ~
    get[String]("Module.homepage") map {
      case n~f~a~ai~d~h => Module(n,f,a,ai,d,h)
    }
  }
  
  val versionParser = {
    get[String]("ModuleRelease.version") ~
    get[java.util.Date]("ModuleRelease.publishedDate") ~
    get[String]("ModuleRelease.frameworkMatch") ~
    get[Boolean]("ModuleRelease.isDefault") map {
      case v~p~m~d => Release(v,p,m,d)
    }
  }
  
  def findEverything: Seq[(Module,Seq[Release])] = DB.withConnection { implicit c =>
     SQL("""
        select * from Module 
        join ModuleRelease on Module.id = ModuleRelease.module_id 
      """).as(parser ~ versionParser *).groupBy(_._1).mapValues(_.map(_._2)).toSeq
  }

  def findAll(keyword: String = ""): Seq[Module] = DB.withConnection { implicit c =>
    SQL("select * from Module where fullname like {keyword} order by name").on('keyword -> ("%" + keyword + "%")).as(parser *)
  }

  def findById(name: String): Option[(Module,Seq[Release])] = DB.withConnection { implicit c =>
    val result = SQL("""
      select * from Module 
      left join ModuleRelease on Module.id = ModuleRelease.module_id 
      where name = {name}
    """).on('name -> name).as( parser ~ (versionParser?) *)
    
    result.headOption.map {
      case module ~ _ => (module, result.flatMap(_._2).sortBy(_.date).reverse)
    }
  }

  
}