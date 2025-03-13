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
      Promise.apply( (_, reject) =>
        dom.window.setTimeout(() => reject(new Exception(s"Refresh timeout after ${delay.toMillis} ms")), delay.toMillis.toDouble)
      )

  def fetchProductsBySku(skus: List[String]): Future[List[Product]] = {
    val url: RequestInfo = s"$baseUrl/products?skus=${skus.mkString(",")}"
    println(s"url = $url")
    val delay: FiniteDuration = 5000.millis
    val maxRetries: Int = 5

    def fetchWithRetry(retriesLeft: Int): Future[List[Product]] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), timeoutPromise(delay))).toFuture

      racePromise.flatMap { response =>
        if (!response.ok) throw new Exception(s"fetchProductsBySku HTTP Error: ${response.status}")
        response.text().toFuture
      }.map { json =>
        upickle.default.read[List[Product]](json)
      }.recoverWith {
        case error: Throwable =>
          println(s"fetchProductsBySku Request failed: ${error.getMessage}. Retries left: $retriesLeft")
          if (retriesLeft > 0) fetchWithRetry(retriesLeft - 1)
          else {
            println("fetchProductsBySku Data is unavailable. Please check your connection.")
            dom.window.alert("fetchProductsBySku Data is unavailable. Please check your connection.")
            Future.failed(error)
          }
      }
    }
    fetchWithRetry(maxRetries)
  }


  def fetchProduct(category: String, sku: String): Unit = {
    val maxRetries: Int = 5
    val url: RequestInfo = s"$baseUrl/$category/$sku"
    val delay: FiniteDuration = 1500.millis //3.seconds

    def fetchWithRetry(retriesLeft: Int): Future[Unit] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), timeoutPromise(delay))).toFuture

      racePromise.flatMap {response =>
        if (!response.ok) throw new Exception(s"Pr.HTTP Error: ${response.status}")
        response.text().toFuture
      }.map { json =>
        println(json)
        val product = upickle.default.read[Product](json)  
        productVar.set(Some(product))
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

    def fetchWithRetry(retriesLeft: Int): Future[Unit] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), timeoutPromise(delay))).toFuture

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

    def fetchWithRetry(retriesLeft: Int): Future[Unit] = {
      val racePromise = Promise.race(js.Array(dom.fetch(url), timeoutPromise(delay))).toFuture

      racePromise.flatMap {response =>
        if (!response.ok) throw new Exception(s"CaPr.HTTP Error: ${response.status}")
        response.text().toFuture
      }.map { json =>
        println(json)
        val categoryProducts = upickle.default.read[List[Product]](json)  
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

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      body = upickle.default.write(loginParameter)   // to Json
      headers = new dom.Headers {
        append("Content-Type", "application/json")
      }
    }

    val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

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

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      body = upickle.default.write(registerParameter)   // to Json
      headers = new dom.Headers {
        append("Content-Type", "application/json")
      }
    }

    val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

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
    val url: RequestInfo = s"$baseUrl/refresh"
    val delay: FiniteDuration = 10000.millis

    val request = new dom.RequestInit {
      method = HttpMethod.POST
      credentials = RequestCredentials.include
    }

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

  // fetch for insert Favorite
  def addToFavorites(productSku: String): Future[Either[String, List[Product]]] = {
    val url: RequestInfo = s"$baseUrl/favorite"
    val delay: FiniteDuration = 10000.millis
    def tokenOpt = currentUserVar.now().map(_.token)

    tokenOpt match {
      case Some(token) =>
        val request = js.Dynamic.literal(
          method = "POST", 
          headers = js.Dictionary(
            "Content-Type" -> "application/json",
            "Authorization" -> s"Bearer $token"
          ),
          body = upickle.default.write(FavoriteRequest(productSku))
        ).asInstanceOf[dom.RequestInit]// }

    //    print(s"TRY ADD THRu FETCH  $productSku")
        val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

        racePromise.flatMap { response =>
         // println("i GOT ANSWER")
          if (!response.ok) throw new Exception(s"${response.status}")
          response.text().toFuture
        }.map {responseBody => 
        //  println(s"--RESPONSE body: $responseBody")
          Right(upickle.default.read[List[Product]](responseBody))
        }.recover { case error: Throwable => {
            Left(s"add To Favorites error: ${error.getMessage}")
          }
        }
      case None => 
        Future.successful(Left("User is not authenticated"))
    }
  }


  // fetch for delete Favorite
  def deleteFromFavorites(productSku: String): Future[Either[String, List[Product]]] = {
    val url: RequestInfo = s"$baseUrl/favorite/$productSku"
    val delay: FiniteDuration = 10000.millis
    def tokenOpt = currentUserVar.now().map(_.token)

    tokenOpt match {
      case Some(token) =>
        val request = new dom.RequestInit {
          method = HttpMethod.DELETE
          headers = js.Dictionary(
            "Authorization" -> s"Bearer $token"
          )
        }

        val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

        racePromise.flatMap { response =>
          if (!response.ok) throw new Exception(s"${response.status}")
          response.text().toFuture
        }.map {responseBody =>
          Right(upickle.default.read[List[Product]](responseBody))//: List[Product])
        }.recover { case error: Throwable =>
            Left(s"delete from Favorites error: ${error.getMessage}")//: Either[String, List[Product]]
        }
      case None =>
        Future.successful(Left("User is not authenticated"))
    }
  }

  // fetch Favorites By UserId
  def fetchUsersFavorites(): Future[Either[String, List[Product]]] = {
    val url: RequestInfo = s"$baseUrl/favorites"
    val delay: FiniteDuration = 10000.millis
    def tokenOpt = currentUserVar.now().map(_.token)

    tokenOpt match {
      case Some(token) =>
        val request = new dom.RequestInit {
          method = HttpMethod.GET
          headers = js.Dictionary(
            "Authorization" -> s"Bearer $token"
          )
        }
        //println(s"HTTPclient. START racePromise")
        val racePromise = Promise.race(js.Array(dom.fetch(url, request), timeoutPromise(delay))).toFuture

        racePromise.flatMap { response =>
          if (!response.ok) {
            //println("HTTPclient. RESPONCSE DONT OK")
            throw new Exception(s"${response.status}")
          }
          response.text().toFuture
        }.map {responseBody =>
         // println(s"HTTPclient. BODY: $responseBody")
          Right(upickle.default.read[List[Product]](responseBody))//: List[Product])
        }.recover { case error: Throwable =>
            Left(s"fetch Favorites By UserId. error: ${error.getMessage}")//: Either[String, List[Product]]
        }
      case None =>
        Future.successful(Left("User is not authenticated"))
    }
  }

end HttpClient
