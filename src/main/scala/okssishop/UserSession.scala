package frontend

import com.raquo.airstream.core.{Observer, Signal}
//import api.AuthApi
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object UserSession {
  val currentUserVar = Var(Option.empty[SessionInfo])

  def tryRefreshTokens(): Unit = {
    //val refreshTokenOpt = dom.document.cookie
    //  .split("; ")
    //  .find(_.startsWith("refreshToken="))
    //  .map(_.stripPrefix("refreshToken="))
    //refreshTokenOpt match {
      //case Some(refreshToken) => 
        HttpClient.fetchNewTokens().foreach {
          case Some(newSession) => currentUserVar.set(Some(newSession))
          case None => println("Token refresh failed. Staying in Guest mode")
        }
     // case None =>  println("active Refresh Token in Cookies is missing. Stay in Guest")
    //}
  }

  val refreshObserver: Observer[Option[SessionInfo]] = Observer {
    case None => tryRefreshTokens()
    case _ =>
  }


  currentUserVar.signal.addObserver(refreshObserver)(unsafeWindowOwner)

  def logout(): Unit = {
    //dom.document.cookie = "refreshTokens=; Max-Age=0; path=/; Secure; HttpOnly"
    HttpClient.fetchLogout().foreach { _ => currentUserVar.set(None)} // only when request successful
    //currentUserVar.set(None)
  }
}