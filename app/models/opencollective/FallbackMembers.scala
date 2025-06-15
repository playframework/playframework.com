package models.opencollective

/**
 * Pull from OpenCollective on 11/23/2021 so that we have something to fall back to when OpenCollective is either down, or if
 * in development we've exceeded the OpenCollective rate limit quotas.
 *
 * This is also used as the initial data when Play first starts up, and then is soon replaced by the actual data
 * when we've finished pulling it from OpenCollective.
 */
object FallbackMembers {

  // format: off
  val members = Seq(
    OpenCollectiveMember(MemberId = 239969, profile = "https://opencollective.com/informaticon", name = "Informaticon", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/c669fe50-421e-11ec-bfe3-039a607eb9c9.png"), twitter = None, github = None, website = Some("https://www.informaticon.com"), role = "BACKER", `type` =  "ORGANIZATION", isActive = true, totalAmountDonated = 1200),
    OpenCollectiveMember(MemberId = 244080, profile = "https://opencollective.com/nezasa", name = "Nezasa", image = Some("https://logo.clearbit.com/nezasa.com"), twitter = Some("https://twitter.com/nezasatravel"), github = Some("https://github.com/nezasa"), website = Some("https://nezasa.com/"), role = "BACKER", `type` =  "ORGANIZATION", isActive = true, totalAmountDonated = 200),
    OpenCollectiveMember(MemberId = 240840, profile = "https://opencollective.com/wiringbits", name = "Wiringbits", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/24250140-4312-11ec-96b0-97dacff9d2e1.png"), twitter = None, github = Some("https://github.com/wiringbits"), website = Some("https://wiringbits.net"), role = "BACKER", `type` =  "ORGANIZATION", isActive = true, totalAmountDonated = 150),
    OpenCollectiveMember(MemberId = 242089, profile = "https://opencollective.com/japan-scala-association-inc", name = "Japan Scala Association, Inc.", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/4f2222a0-4683-11ec-a5b0-1598616b961a.png"), twitter = Some("https://twitter.com/scalamatsuri"), github = Some("https://github.com/scalamatsuri"), website = Some("https://scalamatsuri.org/"), role = "BACKER", `type` =  "ORGANIZATION", isActive = true, totalAmountDonated = 100),
    OpenCollectiveMember(MemberId = 243341, profile = "https://opencollective.com/chenko", name = "CHENKO", image = Some("https://www.gravatar.com/avatar/bbe6c1410c58d259115bc271b5694f96?default=404"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 50),
    OpenCollectiveMember(MemberId = 242632, profile = "https://opencollective.com/guest-f86ca51d", name = "Takashi Kawachi", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/e2b1c190-47ac-11ec-89cf-37ff60ffac78.png"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 32),
    OpenCollectiveMember(MemberId = 243487, profile = "https://opencollective.com/werktools", name = "WERKTOOLS", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/81e50270-4985-11ec-b49a-431689935dd4.png"), twitter = None, github = None, website = Some("https://werktools.com"), role = "BACKER", `type` =  "ORGANIZATION", isActive = true, totalAmountDonated = 25),
    OpenCollectiveMember(MemberId = 243484, profile = "https://opencollective.com/incognito-b0ffd5b7", name = "Incognito", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 25),
    OpenCollectiveMember(MemberId = 242476, profile = "https://opencollective.com/guest-071e8011", name = "Tomokazu Uehara", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242464, profile = "https://opencollective.com/guest-ed3330c6", name = "Takahiko Tominaga", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242455, profile = "https://opencollective.com/guest-839f0452", name = "Tomoki Mizogami", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242153, profile = "https://opencollective.com/shomatan", name = "Shoma Nishitateno", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/1fb1e170-46bc-11ec-8f1c-df6f164dce9d.jpg"), twitter = Some("https://twitter.com/shoma416"), github = Some("https://github.com/shomatan"), website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242121, profile = "https://opencollective.com/guest-143c73be", name = "Shunsuke Tadokoro", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242110, profile = "https://opencollective.com/taisuke-oe", name = "Taisuke Oe", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/46212e10-468f-11ec-8f1c-df6f164dce9d.jpeg"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242101, profile = "https://opencollective.com/guest-43a1a448", name = "taketora", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 241803, profile = "https://opencollective.com/guest-bacde4c2", name = "Chaabane Jalal", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 241782, profile = "https://opencollective.com/tsuyoshi-yoshizawa", name = "Tsuyoshi Yoshizawa", image = Some("https://www.gravatar.com/avatar/c336d98ff389f3e195d13b50cee16dca?default=404"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 20),
    OpenCollectiveMember(MemberId = 242291, profile = "https://opencollective.com/hayasshi", name = "hayasshi", image = Some("https://www.gravatar.com/avatar/2b3d68169a23966b99cfc481e40e6593?default=404"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 10),
    OpenCollectiveMember(MemberId = 242279, profile = "https://opencollective.com/guest-373428e0", name = "Kenichiro Tanaka", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 10),
    OpenCollectiveMember(MemberId = 242133, profile = "https://opencollective.com/guest-30a977fc", name = "Hajime Nishiyama", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 10),
    OpenCollectiveMember(MemberId = 242113, profile = "https://opencollective.com/yyu", name = "yyu", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/0e7730d0-4690-11ec-a5b0-1598616b961a.png"), twitter = Some("https://twitter.com/_yyu_"), github = Some("https://github.com/y-yu"), website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 10),
    OpenCollectiveMember(MemberId = 240275, profile = "https://opencollective.com/guest-fd516442", name = "Eric Loots", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 7),
    OpenCollectiveMember(MemberId = 244140, profile = "https://opencollective.com/szabo-akos", name = "Szabó Ákos", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 243220, profile = "https://opencollective.com/ishikawa-ryuto", name = "Ishikawa Ryuto", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/21a8b4c0-48f3-11ec-a916-6d882bba6d9b.png"), twitter = Some("https://twitter.com/ishikavvavvavva"), github = Some("https://github.com/ishikawawawa"), website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 242123, profile = "https://opencollective.com/guest-d4ecb64c", name = "Ryuhei Ishibashi", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 242109, profile = "https://opencollective.com/guest-f7d8150e", name = "Takahiro Takashima", image = None, twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 242107, profile = "https://opencollective.com/aoiroaoino", name = "aoiroaoino", image = Some("https://www.gravatar.com/avatar/0b8291daeda1cd55e445af644d402bb0?default=404"), twitter = Some("https://twitter.com/aoiroaoino"), github = Some("https://github.com/aoiroaoino"), website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 242100, profile = "https://opencollective.com/guest-c25d7579", name = "omiend", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/93f74940-468e-11ec-a5b0-1598616b961a.png"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 241103, profile = "https://opencollective.com/raboof", name = "raboof", image = Some("https://opencollective-production.s3.us-west-1.amazonaws.com/15c72430-43ca-11ec-8766-97c861826b5c.png"), twitter = Some("https://twitter.com/raboofje"), github = Some("https://github.com/raboof"), website = Some("https://arnout.engelen.eu"), role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5),
    OpenCollectiveMember(MemberId = 239545, profile = "https://opencollective.com/seth-tisue", name = "Seth Tisue", image = Some("https://www.gravatar.com/avatar/fd3622d109eebf4b6218282aada8c23a?default=404"), twitter = None, github = None, website = None, role = "BACKER", `type` =  "USER", isActive = true, totalAmountDonated = 5)
  )
  // format: on

  /**
   * Code to generate the code above from a current list of members
   */
  def dumpMembers(members: Seq[OpenCollectiveMember]) = {
    def option(s: Option[String])                     = s.fold("None")("Some(\"" + _ + "\")")
    def formatMember(m: OpenCollectiveMember): String = {
      import m._
      // format: off
      s"""OpenCollectiveMember(MemberId = $id, profile = "$profile", name = "$name", image = ${option(image)}, twitter = ${option(twitter)}, github = ${option(github)}, website = ${option(website)}, role = "$role", `type` =  "${`type`}", isActive = $isActive, totalAmountDonated = $totalAmountDonated)"""
      // format: on
    }

    def formatMembers(members: Seq[OpenCollectiveMember]) =
      members.map(member => "        " + formatMember(member)).mkString(",\n")

    s"""Seq(
       |  ${formatMembers(members)}
       |)""".stripMargin
  }

}
