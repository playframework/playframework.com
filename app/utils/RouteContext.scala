package utils

import java.util.regex.Pattern

import scala.collection.concurrent.TrieMap
import scala.util.matching.Regex

object routing {

  // memoizes all the routes, so that the route doesn't have to be parsed, and the resulting regex compiled,
  // on each invocation.
  private val routesCache = TrieMap.empty[StringContext, Regex]

  implicit class RouteContext(sc: StringContext) {
    val route = routesCache.getOrElseUpdate(sc, {
      // "parse" the path
      sc.parts.tail.map { part =>
        if (part.startsWith("*")) {
          // It's a .* matcher
          "(.*)" + Pattern.quote(part.drop(1))
        } else if (part.startsWith("<") && part.contains(">")) {
          // It's a regex matcher
          val splitted = part.split(">", 2)
          val regex = splitted(0).drop(1)
          "(" + regex + ")" + Pattern.quote(splitted(1))
        } else {
          // It's an ordinary path part matcher
          "([^/]*)" + Pattern.quote(part)
        }
      }.mkString(Pattern.quote(sc.parts.head), "", "/?").r
    })
  }

}


