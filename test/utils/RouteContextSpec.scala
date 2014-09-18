package utils

import org.specs2.mutable.Specification
import utils.routing.RouteContext

object Id {
  def unapply(s: String) = try {
    Some(s.toLong)
  } catch {
    case e: NumberFormatException => None
  }
}

class RouteContextSpec extends Specification {

  "route interpolation" should {
    "match a plain path" in {
      "match" in {
        "/foo/bar" must beLike {
          case route"/foo/bar" => ok
        }
      }
      "no match" in {
        "/foo/notbar" must beLike {
          case route"/foo/bar" => ko
          case _ => ok
        }
      }
    }
    "match a parameterised path" in {
      "match" in {
        "/foo/testing/bar" must beLike {
          case route"/foo/$id/bar" => id must_== "testing"
        }
      }
      "no match" in {
        "/foo/testing/notbar" must beLike {
          case route"/foo/$id/bar" => ko
          case _ => ok
        }
      }
    }
    "match a regex path" in {
      "match" in {
        "/foo/1234/bar" must beLike {
          case route"/foo/$id<[0-9]+>/bar" => id must_== "1234"
        }
      }
      "no match" in {
        "/foo/123n4/bar" must beLike {
          case route"/foo/$id<[0-9]+>/bar" => ko
          case _ => ok
        }
      }
    }
    "match a star path" in {
      "match" in {
        "/foo/path/to/something" must beLike {
          case route"/foo/$path*" => path must_== "path/to/something"
        }
      }
      "no match" in {
        "/foo/path/to/something" must beLike {
          case route"/foob/$path*" => ko
          case _ => ok
        }
      }
    }
    "match a path with a nested extractor" in {
      "match" in {
        "/foo/1234/bar" must beLike {
          case route"/foo/${Id(id)}/bar" => id must_== 1234l
        }
      }
      "no match" in {
        "/foo/testing/bar" must beLike {
          case route"/foo/${Id(id)}/bar" => ko
          case _ => ok
        }
      }
    }
  }

}
