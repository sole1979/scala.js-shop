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

  import UserSession.currentUserVar

  val baseUrl: String = "http://localhost:8080"

  def timeoutPromise(delay: FiniteDuration): Promise[Nothing] =
      Promise.apply((_, reject) =>
        dom.window.setTimeout(() => reject(new Exception(s"Refresh timeout after ${delay.toMillis} ms")), delay.toMillis.toDouble)
      )


  def fetchProduct(category: String, sku: String): Unit = {
    val maxRetries: Int = 5
    val url: RequestInfo = s"$baseUrl/$category/$sku"
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
    val url: RequestInfo = s"$baseUrl/categories"
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
         // for DevMode:
         // categoriesVar.set(List("DEVoutwear", "DEVlingerie", "DEVsweemsuit"))
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
    val url: RequestInfo = s"$baseUrl/$category"
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

  // fetch for Login
  def login(loginParameter: LoginFormState): Unit = {
    val url: RequestInfo = s"$baseUrl/login"
    val delay: FiniteDuration = 10000.millis

    def sleepReject(delay: FiniteDuration): Promise[Nothing] = Promise.apply((resolve, reject) =>
      dom.window.setTimeout(() => reject(new Exception(s"Login.Timeout exception after delay ${delay.toMillis} ms")), delay.toMillis.toDouble)
    )

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      body = upickle.default.write(loginParameter)   // to Json
      headers = new dom.Headers {
        append("Content-Type", "application/json")
      }
    }

    val racePromise = Promise.race(js.Array(dom.fetch(url, request), sleepReject(delay))).toFuture

    racePromise.flatMap {response =>
      if (!response.ok) throw new Exception(s"Login HTTP Error: ${response.status}")
      response.text().toFuture
    }.map { json =>
      println(json)
      val loginAnswer = upickle.default.read[SessionInfo](json)  //jSON in case class
      currentUserVar.set(Some(loginAnswer))
      loginError.set(None)
    }.recover {
      case error: Throwable => {
        currentUserVar.set(None) //Var(Nil)
        loginError.set(Some(error.getMessage))
        println("Login Request is unavailable. Please check your connection ")
        //dom.window.alert("Login Request is unavailable. Please check your connection")
        Future.failed(error)
      }
    }
  }

  // fetch for Register
  def register(registerParameter: RegisterFormState): Unit = {
    val url: RequestInfo = s"$baseUrl/register"
    val delay: FiniteDuration = 10000.millis

    def sleepReject(delay: FiniteDuration): Promise[Nothing] = Promise.apply((resolve, reject) =>
      dom.window.setTimeout(() => reject(new Exception(s"Register.Timeout exception after delay ${delay.toMillis} ms")), delay.toMillis.toDouble)
    )

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      body = upickle.default.write(registerParameter)   // to Json
      headers = new dom.Headers {
        append("Content-Type", "application/json")
      }
    }

    val racePromise = Promise.race(js.Array(dom.fetch(url, request), sleepReject(delay))).toFuture

    racePromise.flatMap {response =>
      if (!response.ok) throw new Exception(s"Register HTTP Error: ${response.status}")
      response.text().toFuture
    }.map { json =>
      println(json)
      val registerAnswer = upickle.default.read[SessionInfo](json)  //jSON in case class
      currentUserVar.set(Some(registerAnswer))
      registerError.set(None)
    }.recover {
      case error: Throwable => {
        currentUserVar.set(None) //Var(Nil)
        registerError.set(Some(error.getMessage))
        println("Register Request is unavailable. Please check your connection ")
        //dom.window.alert("Login Request is unavailable. Please check your connection")
        Future.failed(error)
      }
    }
  }


  // fetch new tokens
  def fetchNewTokens(): Future[Option[SessionInfo]] = {
   // println("TRY TO REFRESH TOKEN. step1")
    val url: RequestInfo = s"$baseUrl/refresh"
    val delay: FiniteDuration = 10000.millis

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      credentials = RequestCredentials.include
     // body = upickle.default.write(Map("refreshToken" -> refreshToken))
     // headers = new dom.Headers {
      //  append("Content-Type", "application/json")
      //}
    }
   // println("TRY TO REFRESH TOKEN. step2")
    val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

    racePromise.flatMap { response =>
      if (!response.ok) throw new Exception(s"Refresh HTTP Error: ${response.status}")
      response.text().toFuture
    }.map { json =>
      println(json)
      Some(upickle.default.read[SessionInfo](json))
    }.recover {
      case error: Throwable => {
        println(s"Refresh Request failed: ${error.getMessage}")
        None
      }
    }
  }

  // fetch for logout
  def fetchLogout(): Future[Unit] = {
    val url: RequestInfo = s"$baseUrl/logout"
    val delay: FiniteDuration = 5000.millis

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      credentials = RequestCredentials.include
    }

    val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

    racePromise.flatMap { response =>
      if (!response.ok) throw new Exception(s"Logout HTTP Error: ${response.status}")
      response.text().toFuture
    }.map (_ => ()).recover {
      case error: Throwable => {
        println(s"Logouth Request failed: ${error.getMessage}")
      }
    }
  }



end HttpClient
