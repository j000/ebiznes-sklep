package models

import slick.jdbc._
import slick.additions.AdditionsProfile
import slick.additions.entity._
import play.api.libs.json._
import play.api.libs.functional.syntax._

trait SlickProfile extends SQLiteProfile with AdditionsProfile {
  object myApi extends API with AdditionsApi
  override val api = myApi
}

object SlickProfile extends SlickProfile
