package controllers.documentation

import play.api.routing.Router
import play.api.test._

object DocumentationControllerSpec extends PlaySpecification {

  "DocumentationController" should {

    "render a page" in new WithApplication with Injecting {
      inject[Router] // makes sure generated router.Routes class (in target folder) get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
      val reverseRouter: ReverseRouter = inject[ReverseRouter]
      private val page: String         = reverseRouter.page(None, "2.5.x", "Home")
      page must beEqualTo("/documentation/2.5.x/Home")
      val request = FakeRequest("GET", page)
      val result  = await(route(app, request).get)
      result.header.status must beEqualTo(OK)
    }

    "add canonical header" in new WithApplication with Injecting {
      inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
      val reverseRouter: ReverseRouter = inject[ReverseRouter]
      val request                      = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "Home"))
      val result                       = await(route(app, request).get)
      result.header.headers.contains("Link") must beTrue
    }

    "when switching to another version" should {
      "redirect to the selected version and page" in new WithApplication with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]

        val request = FakeRequest("GET", reverseRouter.switch(None, "2.7.x", "Home"))
        val result  = route(app, request).get

        redirectLocation(result) must beSome(reverseRouter.page(None, "2.7.x", "Home"))
      }

      "redirect to the selected version and home when no page is selected" in new WithApplication
        with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]

        val request = FakeRequest("GET", reverseRouter.switch(None, "2.7.x", ""))
        val result  = route(app, request).get

        redirectLocation(result) must beSome(reverseRouter.page(None, "2.7.x", "Home"))
      }

      "redirect to the selected version when not having a trailing slash" in new WithApplication
        with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]

        val page    = reverseRouter.switch(None, "2.7.x", "").stripSuffix("/")
        val request = FakeRequest("GET", page)
        val result  = route(app, request).get

        redirectLocation(result) must beSome(reverseRouter.page(None, "2.7.x", "Home"))
      }
    }

    "when page not found" should {
      "redirect page that was renamed" in new WithApplication with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        // When accessing "AkkaCore" (was renamed to "ThreadPools")
        val request = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "AkkaCore"))
        val result  = route(app, request).get

        // Then we should be redirected to "ThreadPools"
        redirectLocation(result) must beSome(reverseRouter.page(None, "2.5.x", "ThreadPools"))
      }

      "redirect page that was removed" in new WithApplication with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        val request                      = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "PullRequests"))
        val result                       = route(app, request).get

        redirectLocation(result) must beSome(
          "https://github.com/playframework/.github/blob/main/CONTRIBUTING.md",
        )
      }

      "not redirect when there is no new page" in new WithApplication with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        val request                      = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "DoesNotExists"))
        val result                       = route(app, request).get

        redirectLocation(result) must beNone
        await(result).header.status must beEqualTo(NOT_FOUND)
      }

      "not redirect an existing page" in new WithApplication with Injecting {
        inject[Router] // makes sure router.Routes class get initialized, which injects controllers.documentation.Router and calls its withPrefix(...)
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        // AkkaCore exists for version 2.0
        val request = FakeRequest("GET", reverseRouter.page(None, "2.0.x", "AkkaCore"))
        val result  = route(app, request).get

        redirectLocation(result) must beNone
        await(result).header.status must beEqualTo(OK)
      }
    }

  }
}
