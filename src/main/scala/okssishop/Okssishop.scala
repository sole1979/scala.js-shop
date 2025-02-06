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

import org.scalajs.dom.html.Anchor

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
  val isLeftMenuVisible = Var(false)
  val currentLeftMenuVar: Var[String] = Var("main")

  def appElement(): Element = 
    HttpClient.fetchCategories()
//    categoriesVar.set(List("outwear", "lingerie", "sweemsuit")) 

    div(
      // top menu
      renderTopMenu(),
      // main app content
      div(
        h1("OkssiShop !!"),
        child <-- router.currentPageSignal.map {
          case PageHome => renderHomePage()
          case PageCategory(category) => renderCategoryPage(category)
          case PageItem(category, itemCode) => renderItemPage(category, itemCode)
        }
      ),
      // Left maybe visible menu
      child.maybe <-- isLeftMenuVisible.signal.map { isVisible =>
        if (isVisible) {
          currentLeftMenuVar.set("main")
          Some(renderLeftPanelOverlay())
        } else None
      }
    )
  end appElement

  private def renderTopMenu(): Div =
    div(
      position.fixed,
      top := "0",
      left := "0",
      width := "100%",
      backgroundColor := "#333",
      color := "white",
      padding := "10px",
      zIndex := "1001",
      display.flex,
    //  alignItems.center,
   //   justifyContent.spaceBetween,
      button(
        "Menu",
        backgroundColor := "#555",
        color := "white",
        border := "none",
        padding := "5px 10px",
        cursor.pointer,
        onClick --> { _ =>
          isLeftMenuVisible.update(!_)
        },
      ),
      span("OkssiShop", fontSize := "20px",  marginLeft := "50px", fontWeight.bold),
      child.maybe <-- userVar.signal.map(_.map { user =>
          div(color := "green", marginLeft := "50px", s"${user.name}")
        })
    )

  private def renderLeftPanelOverlay(): Div = 
    div(
      marginTop := "30px",
      position.fixed,
      top := "0",
      left := "0",
      width := "300px",
      height := "100%",
      backgroundColor := "white",
      borderRight := "1px solid #ccc",
      boxShadow := "2px 0 5px rgba(0, 0, 0, 0.1)",
      padding := "20px",
      zIndex := "1000",
      //renderAuthMenu()
      child <-- currentLeftMenuVar.signal.map {
        case "main" => renderMainMenu()
        case "auth" => renderAuthMenu()
        case "login" => renderLoginMenu()
      }
    )

  private def renderMainMenu(): Div =
    div(
      div(
        display.flex,
        alignItems.center,
        button(
          backgroundColor := "white",
          color := "Black",
          "<",
         // marginTop := "20px",
          padding := "5px 10px",
          cursor.pointer,
          onClick.mapTo(false) --> isLeftMenuVisible
        ),
        span(
          "OkssiShop",
          color := "black",
          fontSize := "24px",
          lineHeight := "1.5",
          marginLeft := "50px"
        )
      ),
      renderListCategory(),
      button(
        "Client Access",
        marginTop := "20px",
        padding := "5px 10px",
        cursor.pointer,
        onClick.mapTo("auth") --> currentLeftMenuVar
      )
    )

  
  private def renderListCategory(): Div =
    div(
      styleAttr := "display: flex; flex-direction: column; gap: 10px; padding: 3px; background-color: #f8f8f8;",
      textAlign := "left",
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

end Main
