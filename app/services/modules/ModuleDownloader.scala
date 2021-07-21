package services.modules

/**
 * creates a set or curl commands to download all play1 modules from S3
 */
object ModuleDownloader extends App {

  InMemDatabase.rawModules
    .flatMap {
      case (moduleId, module) =>
        InMemDatabase.rawReleases.flatMap {
          case (mid, release) if(mid == moduleId )=>
            val filename = s"${module.name}-${release.version}.zip"
            Some(s"curl -X GET https://downloads.typesafe.com/play1/modules/$filename -o $filename")
          case _ => None
        }
    }
    .foreach(println)

}
