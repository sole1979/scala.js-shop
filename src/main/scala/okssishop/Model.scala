package frontend

import scala.scalajs.js
//import scala.scalajs.js.annotation.*
import scala.scalajs.js.annotation.JSImport
import com.raquo.laminar.api.L.{*, given}

//import upickle.default.{ReadWriter, Reader, Writer, macroW, macroR}
import upickle.default._
import upickle.default.{ReadWriter => RW, macroRW}
import scala.scalajs.js.JSON

case class Product(sku: String, category: String, name: String, price: BigDecimal, descr: String, srcImg: String)
object Product {
  implicit val bigDecimalRW: ReadWriter[BigDecimal] = readwriter[String]
    .bimap[BigDecimal](_.toString, s => BigDecimal(s))

  implicit val rw: ReadWriter[Product] = macroRW
}

val productVar: Var[Option[Product]] = Var(None) // None, пока данные не загружены
val categoriesVar: Var[List[String]] = Var(List.empty)
val categoryProductsVar: Var[List[Product]] = Var(List.empty)
//val userVar = Var(Option.empty[LoginAnswer])

case class LoginFormState(email: String, password: String)
object LoginFormState {
  implicit val rw: ReadWriter[LoginFormState] = macroRW
}

case class RegisterFormState(name: String, email: String, password: String)
object RegisterFormState {
  implicit val rw: ReadWriter[RegisterFormState] = macroRW
}

case class SessionInfo(name: String, token: String)
object SessionInfo {
  implicit val rw: ReadWriter[SessionInfo] = macroRW
}

