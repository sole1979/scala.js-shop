package frontend

import com.raquo.airstream.core.Signal
import com.raquo.waypoint._
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

//import model.*

def renderCategoryPage(category: String): HtmlElement =
  categoryProductsVar.set(List.empty) 
  HttpClient.fetchCategoryProducts(category)

  div(
    h2("Category Now!!!"),
    h2(s"Category $category Page"),
    a(
      href := router.absoluteUrlForPage(PageHome),
     // onClick.preventDefault --> (_ => router.pushState(PageHome)),
      "Press For Page-Home!"
    ),

   div(
    display.flex,
    flexWrap.wrap,
    justifyContent.flexStart,
    gap("10px"),
 //   children <-- Model.dataVar.signal.map { items =>
 //     items.map { item =>
    children <-- categoryProductsVar.signal.map { products =>
      products.map { product =>
        a(
          display.block,
          width := "1 1 calc(25% - 20px)",    //flex
          boxSizing.borderBox,
          border := "1px solid #ddd",
          padding := "2px",
          textAlign.center,

          //содержание карточки
          //href := router.absoluteUrlForPage(PageItem(item.category, item.itemCode)),//item.srcOut,
          href := router.absoluteUrlForPage(PageItem(product.category, product.sku)),
          //target := "_blank",
          img(
            src := product.srcImg,
      //      alt := "COAT",
            maxWidth :=  "100%",
            height := "auto",
            borderRadius := "10px"
          ),
          h3(
            product.name,
            fontSize := "24px",
            fontWeight.bold,
            textAlign.left
          ),
          p(
            s"Articul: ${product.sku}",
            fontSize := "10px",
            color := "green",
            textAlign.left
          ),
          p(s"Price: ${product.price}",
            textAlign.left
          ),
          p(
            s"About: ${product.descr}",
            fontSize := "12px",
            color := "red",
            textAlign.left
          )
        )
      }
    }
  )


  )
end renderCategoryPage

