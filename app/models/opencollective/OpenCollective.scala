package models.opencollective

import play.api.libs.json.Json
import play.api.libs.json.Reads

case class OpenCollectiveMember(
    MemberId: Long,            // 123456,
    profile: String,           // https://opencollective.com/john-doe,
    name: String,              // John Doe,
    image: Option[String],     // https://www.gravatar.com/avatar/81956674664e179d682db2e431eb4369?default=404,
    twitter: Option[String],   // https://twitter.com/john-doe,
    github: Option[String],    // https://github.com/john-doe,
    website: Option[String],   // https://john-doe.com
    `type`: String,            // USER or ORGANIZATION,
    role: String,              // BACKER, ADMIN or HOST,
    isActive: Boolean,         // true or false
    totalAmountDonated: Double, // amount in cent (USD 100.00 = 10000)
    // createdAt: 2021-11-08 14:41,
    // lastTransactionAt: 2021-11-19 09:16,
    // lastTransactionAmount: 0,
    // company: "Facebook Inc" or null,
    // description: "Scala enthusiast" or null,
    // email: "john.doe@example.com" or null,
) {
  def id   = MemberId
  def slug = profile.substring("https://opencollective.com/".size)
  def link = website.getOrElse(twitter.getOrElse(github.getOrElse(profile)))

  /**
   * The "image" property is null for OpenCollective guest users.
   * Usually the "image" points directly to where the image is hosted (like gravatar or aws), however it seems
   * OpenCollective provides a proxy via https://images.opencollective.com/$slug/avatar/<size>.png for each image,
   * that's what we can use for guest users to display an image that contains the initials of the name.
   * However: Users with name "Guest" don't even provide an avatar via the OpenCollective proxy - they just don't have an image!
   *
   * Because docs about this are missing: OpenCollective offers following urls to fetch a profile picture:
   * - https://images.opencollective.com/$slug/avatar.png (128x128)
   * - https://images.opencollective.com/$slug/avatar/<size>.png
   * To avoid caching problems you can insert a random string in the url (e.g. "47f8900d")
   * - https://images.opencollective.com/$slug/<random>/avatar.png (128x128)
   * - https://images.opencollective.com/$slug/<random>/avatar/<size>.png
   */
  def imageUrl = image.getOrElse(s"https://images.opencollective.com/$slug/avatar/256.png")
}

object OpenCollectiveMember {
  implicit val jsonReads: Reads[OpenCollectiveMember] = Json.reads[OpenCollectiveMember]
}
