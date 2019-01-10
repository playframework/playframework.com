package controllers.documentation

import play.api.test._

object DocumentationControllerSpec extends PlaySpecification {

  "DocumentationController" should {

    "render a page" in new WithApplication with Injecting {
      val reverseRouter: ReverseRouter = inject[ReverseRouter]
      val request = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "Home"))
      val result = await(route(app, request).get)
      result.header.status must beEqualTo(OK)
    }

    "add canonical header" in new WithApplication with Injecting {
      val reverseRouter: ReverseRouter = inject[ReverseRouter]
      val request = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "Home"))
      val result = await(route(app, request).get)
      result.header.headers.contains("Link") must beTrue
    }

    "when page not found" should {
      "redirect page that was renamed" in new WithApplication with Injecting {
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        // When accessing "AkkaCore" (was renamed to "ThreadPools")
        val request = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "AkkaCore"))
        val result = route(app, request).get

        // Then we should be redirected to "ThreadPools"
        redirectLocation(result) must beSome(reverseRouter.page(None, "2.5.x", "ThreadPools"))
      }

      "redirect page that was removed" in new WithApplication with Injecting {
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        val request = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "PullRequests"))
        val result = route(app, request).get

        redirectLocation(result) must beSome("https://www.playframework.com/contributing")
      }

      "not redirect when there is no new page" in new WithApplication with Injecting {
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        val request = FakeRequest("GET", reverseRouter.page(None, "2.5.x", "DoesNotExists"))
        val result = route(app, request).get

        redirectLocation(result) must beNone
        await(result).header.status must beEqualTo(NOT_FOUND)
      }

      "not redirect an existing page" in new WithApplication with Injecting {
        val reverseRouter: ReverseRouter = inject[ReverseRouter]
        // AkkaCore exists for version 2.0
        val request = FakeRequest("GET", reverseRouter.page(None, "2.0.x", "AkkaCore"))
        val result = route(app, request).get

        redirectLocation(result) must beNone
        await(result).header.status must beEqualTo(OK)
      }
    }

  }
}
