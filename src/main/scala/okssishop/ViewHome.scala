package frontend
import com.raquo.airstream.core.Signal
//import com.raquo.waypoint._
//import com.raquo.waypoint.Router

import scala.scalajs.js
//import scala.scalajs.js.annotation.*
//import scala.annotation.targetName
//import scala.reflect.ClassTag

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._

import org.scalajs.dom

//import upickle.default.ReadWriter
//import upickle.default._
//import urldsl.vocabulary.UrlMatching
//import upickle.default.{ReadWriter => RW, macroRW}


def renderHomePage(): HtmlElement =
  div(
    h2("Home Page"),
    h3("Этот сайт для тебя, дорогой Покупатель! Он сделан с любовью!!"),
    a(
      href := router.absoluteUrlForPage(PageCategory("outewrar")),
    //  onClick.preventDefault --> (_ => router.pushState(PageCategory("outewear"))),
      "Press For Page-Category -outewear-"
    )
  )
end renderHomePage

