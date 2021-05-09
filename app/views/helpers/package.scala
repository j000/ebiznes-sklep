package views.html

package object helpers {
  import views.html.helper.FieldConstructor
  implicit val fieldConstructor = FieldConstructor(views.html.helpers.InputTemplate.f)
}
