package frontend

import scala.scalajs.js
//import scala.scalajs.js.annotation.*
import com.raquo.laminar.api._
import com.raquo.laminar.api.L.*

import com.raquo.waypoint._
//import upickle.default.ReadWriter
import upickle.default._
//import urldsl.vocabulary.UrlMatching
import upickle.default.{ReadWriter => RW, macroRW}


sealed trait Page derives ReadWriter  //for ver 4.0

case object PageHome extends Page
case class PageCategory(category: String) extends Page
case class PageItem(category: String, itemCode: String) extends Page

val homeRoute = Route.static(PageHome, root / endOfSegments)

val categoryRoute = Route[PageCategory, String](
  encode = pageCategory => pageCategory.category,
  decode = arg => PageCategory(category = arg),
  pattern = root / segment[String] / endOfSegments
)

val itemRoute = Route[PageItem, (String, String)](
  encode = pageItem => (pageItem.category, pageItem.itemCode),
  decode = args => PageItem(category = args._1, itemCode = args._2),
  pattern = root / segment[String] / segment[String] / endOfSegments
)

val router = new Router[Page](
  routes = List(homeRoute, categoryRoute, itemRoute),
  getPageTitle = {
    case PageHome     => "Okssi Home"
    case PageCategory(arg) => s"OkSSi $arg"
    case PageItem(cat, code) => s"OkSSi $cat $code"
  },
  serializePage = page => upickle.default.write(page),
  deserializePage = str => upickle.default.read[Page](str)
  )(
    popStateEvents = L.windowEvents(_.onPopState),
    owner = L.unsafeWindowOwner
  )

//---
