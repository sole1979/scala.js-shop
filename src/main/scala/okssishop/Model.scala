package frontend

import scala.scalajs.js
//import scala.scalajs.js.annotation.*
import scala.scalajs.js.annotation.JSImport
import com.raquo.laminar.api.L.{*, given}

//import upickle.default.{ReadWriter, Reader, Writer, macroW, macroR}
import upickle.default._
import upickle.default.{ReadWriter => RW, macroRW}
import scala.scalajs.js.JSON

//def fromJson[T: Reader](a: js.Any) = upickle.default.read[T](JSON.stringify(a))
//def toJsonString[T: Writer](t: T) = upickle.default.write(t)

case class Product(sku: String, category: String, name: String, price: BigDecimal, descr: String, srcImg: String)
  
//object Responses:
object Product {
  implicit val bigDecimalRW: ReadWriter[BigDecimal] = readwriter[String]
    .bimap[BigDecimal](_.toString, s => BigDecimal(s))

  implicit val rw: ReadWriter[Product] = macroRW
}
  //given Reader[Product] = macroR[Product]

//end Responses

//object Payloads:
//end Payloads
val productVar: Var[Option[Product]] = Var(None) // None, пока данные не загружены


case class  DataItem(itemCode: String, category: String, name: String, price: Double, descr: String, srcImg: String, srcOut: String)
//добавляем методы здесь

//object DataItem:
//здесь добавляем дополнительные методы
//+метод apply()
//end DataItem

object Model:
  @js.native @JSImport("/img/kurtka1.jpg", JSImport.Default)
  val srcImg1: String = js.native
  val srcOut1: String = router.absoluteUrlForPage(PageItem("outewear", "jacket"))

  @js.native @JSImport("/img/kurtka2.jpg", JSImport.Default)
  val srcImg2: String = js.native
  val srcOut2: String = "https://developer.mozilla.org/en-US/docs/Web/JavaScript"

  @js.native @JSImport("/img/kurtka3.jpg", JSImport.Default)
  val srcImg3: String = js.native
  val srcOut3: String = "https://developer.mozilla.org/en-US/docs/Web/JavaScript"

  @js.native @JSImport("/img/kurtka4.jpg", JSImport.Default)
  val srcImg4: String = js.native
  val srcOut4: String = "https://developer.mozilla.org/en-US/docs/Web/JavaScript"

  @js.native @JSImport("/img/kurtka5.jpg", JSImport.Default)
  val srcImg5: String = js.native
  val srcOut5: String = "https://developer.mozilla.org/en-US/docs/Web/JavaScript"

  @js.native @JSImport("/img/kurtka6.jpg", JSImport.Default)
  val srcImg6: String = js.native
  val srcOut6: String = "https://developer.mozilla.org/en-US/docs/Web/JavaScript"
//end Model


//final class Model:
  //  val ItemVar: Var[DataItem] = Var()
    val dataVar: Var[List[DataItem]] = 
    Var(
      List(
        DataItem("one", "outewear", "Coat", 100, "Goood Coat", Model.srcImg1, Model.srcOut1), 
        DataItem("two", "outewear", "Jacket", 200, "Amazing Jacket", Model.srcImg2, Model.srcOut2),
        DataItem("three", "outewear", "Bikini", 300, "Crazy Bikini", Model.srcImg3, Model.srcOut3),
        DataItem("for", "outewear", "Stocking1", 350, "Amazing super sexy stocking", Model.srcImg4, Model.srcOut4),
        DataItem("five", "outewear", "Stocking2", 450, "Best stocking", Model.srcImg5, Model.srcOut5),
        DataItem("six", "outewear", "Stocking3", 550, "Crazy stocking", Model.srcImg6, Model.srcOut6)
        )
      )
//def addDataItem(item: DataItem): Unit = ???
//def removeDataItem(itemCode: String): Unit = ???
//def makeDataItemUpdater...
end Model 
  //end setupCounter