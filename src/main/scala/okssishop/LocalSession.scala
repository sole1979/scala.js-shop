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



//  val currentUserVar = Var(Option.empty[SessionInfo])
//  val favoritesVar: Var[List[Product]] = Var(List.empty)
//  val isAuthenticated: Var[Boolean] = Var(false)

object LocalSession {
 // val localFavoritesVar: Var[List[String]] = Var(getFavorites())
 
  private val Key = "localFavorites"

  def getFavorites(): List[String] = {
    Option(dom.window.localStorage.getItem(Key))
      .filter(_.nonEmpty)   // sequence is not empty
      .map(json => upickle.default.read[List[String]](json))
      .getOrElse(Nil)  // if data is missing, return empty List    }
  }

  def saveFavorites(favorites: List[String]): Unit = {
    dom.window.localStorage.setItem(Key, upickle.default.write(favorites))
  }

  def addFavorite(sku: String): List[String] = {
    val updated = (getFavorites() :+ sku).distinct
    saveFavorites(updated)
    updated
  }

  def removeFavorite(sku: String): List[String] = {
    val updated = getFavorites().filterNot(_ == sku)
    saveFavorites(updated)
    updated
  }


}



