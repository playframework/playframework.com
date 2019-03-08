package models

object Platform extends Enumeration {
  type Platform = Value
  val WIN, MAC, LINUX = Value

  def apply(maybePlatform: Option[String]): Platform = {

    maybePlatform.getOrElse("win") match {
      case s if s.equalsIgnoreCase("win")   => WIN
      case s if s.equalsIgnoreCase("mac")   => MAC
      case s if s.equalsIgnoreCase("linux") => LINUX

      case s if s.contains("Windows") => WIN
      case s if s.contains("Mac")     => MAC
      case s if s.contains("Linux")   => LINUX
      case _                          => WIN
    }

  }
}
