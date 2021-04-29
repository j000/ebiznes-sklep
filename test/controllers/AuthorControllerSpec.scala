package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class AuthorControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "AuthorController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new AuthorController(stubControllerComponents())
      val author = controller.index().apply(FakeRequest(GET, "/"))

      status(author) mustBe OK
      contentType(author) mustBe Some("text/plain")
      contentAsString(author) must include ("list")
    }

    "render the index page from the application" in {
      val controller = inject[AuthorController]
      val author = controller.index().apply(FakeRequest(GET, "/"))

      status(author) mustBe OK
      contentType(author) mustBe Some("text/plain")
      contentAsString(author) must include ("list")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/api/author")
      val author = route(app, request).get

      status(author) mustBe OK
      contentType(author) mustBe Some("text/plain")
      contentAsString(author) must include ("list")
    }
  }
}
