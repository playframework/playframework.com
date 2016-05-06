package models.modules

/**
 * A module
 */
case class Module(name: String, fullname: String, author: String, authorId: String, description: String, homePage: String)

/**
 * A release of a module
 */
case class Release(version: String, date: java.util.Date, frameworkMatch: String, isDefault: Boolean)