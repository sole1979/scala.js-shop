package frontend

import com.raquo.airstream.core.{Observer, Signal}
//import api.AuthApi
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalajs.dom.window
import upickle.default._
import scala.scalajs.js.timers.setTimeout


val favoritesVar: Var[List[String]] = Var(List.empty)
val favoritesProductsVar: Var[List[Product]] =  Var(Nil)

object UserSession {
  val currentUserVar = Var(Option.empty[SessionInfo])
  //val favoritesVar: Var[List[String]] = Var(List.empty)
 // val isAuthenticated =  currentUserVar.now().isDefined)
  
//----


  def tryRefreshTokens(): Unit = {
    println("--------I TRY TO GET NEW TOKEN-----------")
    HttpClient.fetchNewTokens().foreach {
      case Some(newSession) => 
        currentUserVar.set(Some(newSession))
      //  HttpClient.fetchUsersFavorites().foreach {
       //   case Right(favorites) => favoritesVar.set(favorites.map(_.sku))
      //    case Left(error) => println(s"Error download Favorites: $error")
      //  }
      case None => println("Token refresh failed. Staying in Guest mode")
    }
  }

  val refreshObserver: Observer[Option[SessionInfo]] = Observer {
    case None => tryRefreshTokens()
    case _ =>
  }


  currentUserVar.signal.addObserver(refreshObserver)(unsafeWindowOwner)

  def logout(): Unit = {
    HttpClient.fetchLogout().foreach { _ =>
      currentUserVar.set(None)}
      favoritesVar.set(List.empty)

  }
 //-------refreshFavorites----
currentUserVar.signal.foreach { _ =>
    UserSession.loadFavorites()
  }(unsafeWindowOwner)

 
  //---Favorites List SKU
 // val usersFavoritesVar: Var[List[String]] = Var(List.empty)  //refresh with needed event
  //---Favorites List PRODUCTS
  def refreshFavoritesProductsVar(): Unit = {
    HttpClient.fetchProductsBySku(favoritesVar.now())
      .foreach { products => favoritesProductsVar.set(products)}
    //  .recover { case ex => println(s"Error download Favorites products: ${ex.getMassage}") }
  }

  //-----ClientForFavorites---
  def loadFavorites(): Unit = {
    if (currentUserVar.now().isDefined) {
      HttpClient.fetchUsersFavorites().foreach {
        case Right(favorites) => favoritesVar.set(favorites.map(_.sku))
        case Left(error) => println(s"Error get Favorites: error")
      }
    } else {
     // println("---TRY REFRESH Favorites throw LOCAL STORAGE")
    //  favoritesVar.set(LocalSession.getFavorites())
      val tempFav = LocalSession.getFavorites()
     // println(s"---RESULT REFRESH Favorites throw LOCAL STORAGE: $tempFav")
      favoritesVar.set(tempFav)
     // println(s"---FAV VALUE NOW: ${favoritesVar.now()}")
    //  setTimeout(0) {
     //   println(s"---FAV VAL AFTER EVENT LOOP: ${favoritesVar.now()}")
   //   }
    }
  }

  def addFavorite(sku: String): Unit = {
    if (currentUserVar.now().isDefined) {
      HttpClient.addToFavorites(sku).foreach {
        case Right(favorites) => favoritesVar.set(favorites.map(_.sku))
        case Left(error) => println(s"Error addToFavorites: $error")
      }
    } else {
      favoritesVar.set(LocalSession.addFavorite(sku))
    }
  }

  def removeFavorite(sku: String): Unit = {
   if (currentUserVar.now().isDefined) {
      HttpClient.deleteFromFavorites(sku).foreach {
        case Right(favorites) => favoritesVar.set(favorites.map(_.sku))
        case Left(error) => println(s"Error removeFromFavorites: $error")
      }
    } else {
      favoritesVar.set(LocalSession.removeFavorite(sku))
    }
  }

  def toggleFavorite(sku: String): Unit = {
    if (favoritesVar.now().contains(sku)) removeFavorite(sku)//.foreach(favoritesVar.set)
   else addFavorite(sku)//.foreach(favoritesVar.set)
  }



}




