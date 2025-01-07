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

import scala.concurrent.duration._
//val delay: FiniteDuration = 3.seconds


object HttpClient:

  def fetchProduct(category: String, sku: String): Unit = {
    val maxRetries: Int = 5
    val url: RequestInfo = s"http://localhost:8080/$category/$sku"
    val delay: FiniteDuration = 1500.millis //3.seconds

    def sleepReject(delay: FiniteDuration): Promise[Nothing] = Promise.apply((resolve, reject) =>
      dom.window.setTimeout(() => reject(new Exception(s"Pr.Timeout exception after delay ${delay.toMillis} ms")), delay.toMillis.toDouble)
    )

    def fetchWithRetry(retriesLeft: Int): Future[Unit] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), sleepReject(delay))).toFuture

      racePromise.flatMap {response =>
        if (!response.ok) throw new Exception(s"Pr.HTTP Error: ${response.status}")
        //println("Response Ok. Parsing...")
        response.text().toFuture
      }.map { json =>
        println(json)
        val product = upickle.default.read[Product](json)  //jSON in case class
        //println("okey. Product ready: " + product)
        productVar.set(Some(product))   // update Var
      }.recoverWith {
        case error: Throwable => println(s"Pr.Error downloading Product: ${error.getMessage}. Retries left: $retriesLeft")
        if (retriesLeft > 0) fetchWithRetry(retriesLeft - 1)
        else {
          productVar.set(None)
          println("Pr.Data is unavailable. Please check your connection ")
          dom.window.alert("Pr.Data is unavailable. Please check your connection")
          Future.failed(error)
        }
      }
    }
    fetchWithRetry(maxRetries)
  }

  // fetch All Cateries
  def fetchCategories(): Unit = {
    val maxRetries: Int = 5
    val url: RequestInfo = s"http://localhost:8080/categories"
    val delay: FiniteDuration = 1500.millis //3.seconds

    def sleepReject(delay: FiniteDuration): Promise[Nothing] = Promise.apply((resolve, reject) =>
      dom.window.setTimeout(() => reject(new Exception(s"Ca.Timeout exception after delay ${delay.toMillis} ms")), delay.toMillis.toDouble)
    )

    def fetchWithRetry(retriesLeft: Int): Future[Unit] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), sleepReject(delay))).toFuture

      racePromise.flatMap {response =>
        if (!response.ok) throw new Exception(s"Ca.HTTP Error: ${response.status}")
        //println("Response Ok. Parsing...")
        response.text().toFuture
      }.map { json =>
        println(json)
        val categories = upickle.default.read[List[String]](json)  //jSON in case class
        //println("okey. Product ready: " + product)
        categoriesVar.set(categories)   // update Var
      }.recoverWith {
        case error: Throwable => println(s"Ca.Error downloading Categories: ${error.getMessage}. Retries left: $retriesLeft")
        if (retriesLeft > 0) fetchWithRetry(retriesLeft - 1)
        else {
          categoriesVar.set(List.empty) //Var(Nil)
          println("Ca.Data is unavailable. Please check your connection ")
          dom.window.alert("Ca.Data is unavailable. Please check your connection")
          Future.failed(error)
        }
      }
    }
    fetchWithRetry(maxRetries)
  }

  // fetch Product of Category
  def fetchCategoryProducts(category: String): Unit = {
    val maxRetries: Int = 5
    val url: RequestInfo = s"http://localhost:8080/$category"
    val delay: FiniteDuration = 1500.millis //3.seconds

    def sleepReject(delay: FiniteDuration): Promise[Nothing] = Promise.apply((resolve, reject) =>
      dom.window.setTimeout(() => reject(new Exception(s"CaPr.Timeout exception after delay ${delay.toMillis} ms")), delay.toMillis.toDouble)
    )

    def fetchWithRetry(retriesLeft: Int): Future[Unit] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), sleepReject(delay))).toFuture

      racePromise.flatMap {response =>
        if (!response.ok) throw new Exception(s"CaPr.HTTP Error: ${response.status}")
        //println("Response Ok. Parsing...")
        response.text().toFuture
      }.map { json =>
        println(json)
        val categoryProducts = upickle.default.read[List[Product]](json)  //jSON in case class
        //println("okey. Product ready: " + product)
        categoryProductsVar.set(categoryProducts)   // update Var
      }.recoverWith {
        case error: Throwable => println(s"CaPr.Error downloading CategoryProducts: ${error.getMessage}. Retries left: $retriesLeft")
        if (retriesLeft > 0) fetchWithRetry(retriesLeft - 1)
        else {
          categoriesVar.set(List.empty) //Var(Nil)
          println("CaPr.Data is unavailable. Please check your connection ")
          dom.window.alert("CaPr.Data is unavailable. Please check your connection")
          Future.failed(error)
        }
      }
    }
    fetchWithRetry(maxRetries)
  }



end HttpClient
