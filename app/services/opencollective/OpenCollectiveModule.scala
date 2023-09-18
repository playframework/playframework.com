package services.opencollective

import play.api.Configuration
import play.api.Environment
import play.api.inject.Module

class OpenCollectiveModule extends Module {

  def bindings(environment: Environment, configuration: Configuration) = {
    val restApiUrl = configuration.underlying.getString("opencollective.restApiUrl")
    val slug       = configuration.underlying.getString("opencollective.slug")

    Seq(
      bind[OpenCollectiveConfig].to(OpenCollectiveConfig(restApiUrl, slug)),
      bind[OpenCollective].to[DefaultOpenCollective],
      bind[MembersSummariser]
        .qualifiedWith("openCollectiveMembersSummariser")
        .to[DefaultMembersSummariser],
      bind[MembersSummariser].to[CachingMembersSummariser],
    )
  }
}
