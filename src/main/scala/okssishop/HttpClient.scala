package frontend

import scala.scalajs.js
//import scala.scalajs.js.annotation.*
import scala.scalajs.js.annotation.JSImport
import com.raquo.laminar.api.L.{*, given}

//import upickle.default.{ReadWriter, Reader, Writer, macroW, macroR}
import upickle.default._
import upickle.default.{ReadWriter => RW, macroRW}
import scala.scalajs.js.JSON

import org.scalajs.dom.Fetch.fetch
import org.scalajs.dom._
import org.scalajs.dom
import scala.scalajs.js.Thenable.Implicits.*
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.scalajs.js.Promise
import org.scalajs.dom.HttpMethod

//def fromJson[T: Reader](a: js.Any) = upickle.default.read[T](JSON.stringify(a))
//def toJsonString[T: Writer](t: T) = upickle.default.write(t)

//object Responses:
 // case class Product(sku: String, category: String, name: String, price: BigDecimal, descr: String, srcImg: String)
  //object Product:
    //given Reader[Product] = macroR[Product]
//end Responses


object HttpClient:
 // def fetchProduct(category: String, sku: String): Unit = {
   // val url: RequestInfo = s"http://localhost:8080/$category/$sku"
  //    dom.fetch(url)
   //     .`then`{response =>
     //     if (!response.ok) throw new Exception(s"HTTP Error: ${response.status}")
     //     println("Response Ok. Parsing...")
     //     response.text()
    //    }
   //     .`then` { json =>
     //     val product = upickle.default.read[Product](json)  //jSON in case class
    //      productVar.set(Some(product))   // update Var
    //    }
       // .`catch` { error =>
       //  println(s"Error download Product: ${error.getMessage}") // error lan, HTTP and Parse
       //  productVar.set(None)
       // }
//  }

  def fetchProductFuture(category: String, sku: String): Unit = {
    val url: RequestInfo = s"http://localhost:8080/$category/$sku"
      val responseFuture = dom.fetch(url).toFuture

      responseFuture.flatMap {response =>
        if (!response.ok) throw new Exception(s"HTTP Error: ${response.status}")
        //println("Response Ok. Parsing...")
        response.text().toFuture
      }.map { json =>
        println(json)
        val product = upickle.default.read[Product](json)  //jSON in case class
        //println("okey. Product ready: " + product)
        productVar.set(Some(product))   // update Var
      }.recover {
        case error: Throwable => println(s"Error downloading Product: ${error.getMessage}")
        productVar.set(None)
      }
  }
end HttpClient
