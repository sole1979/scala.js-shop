package frontend

import com.raquo.airstream.core.Signal
import com.raquo.waypoint._
//import com.raquo.waypoint.Router

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import scala.annotation.targetName
import scala.reflect.ClassTag

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._

import org.scalajs.dom

//import upickle.default.ReadWriter
import upickle.default._
//import urldsl.vocabulary.UrlMatching
import upickle.default.{ReadWriter => RW, macroRW}

@main
def OkssiShop(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )
end OkssiShop

object Main:
 // val model = new Model
 // import model.*


  def appElement(): Element =
    HttpClient.fetchCategories()

    div(
      h1("OkssiShop !!"),
      child <-- router.currentPageSignal.map {
        case PageHome => renderHomePage()
        case PageCategory(category) => renderCategoryPage(category)
        case PageItem(category, itemCode) => renderItemPage(category, itemCode)
      }
    )

  //  router.initialize()
  end appElement
end Main

 
