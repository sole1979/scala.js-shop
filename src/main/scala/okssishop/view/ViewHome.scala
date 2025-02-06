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
import org.scalajs.dom.html.Anchor

import org.scalajs.dom

//import upickle.default.ReadWriter
//import upickle.default._
//import urldsl.vocabulary.UrlMatching
//import upickle.default.{ReadWriter => RW, macroRW}


def renderHomePage(): HtmlElement =
 div( 
  div(
    h2("Home Page"),
    h3("This Site For You!!"),
    //a(
    //  href := router.absoluteUrlForPage(PageCategory("outewrar")),
    //  onClick.preventDefault --> (_ => router.pushState(PageCategory("outewear"))),
    //  "Press For Page-Category -outewear-"
    //)
  ),

  div(
    styleAttr := "display: flex; flex-direction: column; gap: 10px; padding: 3px; background-color: #f8f8f8;",
    children <-- categoriesVar.signal.map { categories =>
      categories.map { category =>
        a(
          href := router.absoluteUrlForPage(PageCategory(s"$category")),
          textDecoration := "none",
          color := "#333",
          gap := "1px",
          padding := "1px 10px",
          borderRadius := "4px",
          transition := "background-color 0.3s",
          onMouseEnter --> { event =>
            event.target.asInstanceOf[Anchor].style.backgroundColor = "#ddd"
          },
          onMouseLeave -->  { event =>
            event.target.asInstanceOf[Anchor].style.backgroundColor = ""
          },
          category
        )
      }
    }
  )
 )
  //div(
  //  ul(
  //    children <-- categoriesVar.signal.map { categories =>
  //      categories.map { category =>
  //        li(category)
  //      }
  //    }
  //  )
  //)
 //)
end renderHomePage

