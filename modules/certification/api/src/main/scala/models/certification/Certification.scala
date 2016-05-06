package models.certification

import org.joda.time.DateTime

/**
 * Interest in a certification
 */
case class Certification(registered: DateTime, name: String, email: String, developer: Boolean, organization: Boolean, comments: String)
