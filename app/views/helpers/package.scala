package views.html

package object helpers {
  import views.html.helper.FieldConstructor
  import play.api.mvc.RequestHeader
  import play.twirl.api.Html

  implicit val fieldConstructor = FieldConstructor(
    views.html.helpers.InputTemplate.f,
  )

  def inputHidden(
    field: play.api.data.Field,
    args: (Symbol, Any)*,
  )(
    implicit
    request: RequestHeader,
  ): Html = {
    Html(
      s"""<input type="hidden" id="${field.id}" name="${field
        .name}" value="${field.value}">""",
    )
  }

}
