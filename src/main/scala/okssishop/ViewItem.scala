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


def renderItemPage(category: String, itemCode: String): HtmlElement =

 val itemVar = Model.dataVar.signal.map ( items =>
   items.filter(_.itemCode == itemCode)
 )

  productVar.set(None) 
  HttpClient.fetchProductFuture(category, itemCode)

  div(
    h2("Item Page"),
    a(
      href := router.absoluteUrlForPage(PageHome),
 //     onClick.preventDefault --> (_ => router.pushState(PageHome)),
      "Press For Page-Home!"
    ),


    h3(s"item: $category $itemCode"),
    //-----------------------
  //  children <-- itemVar.map { listItem => 
  //    listItem.map { item =>
    child <-- productVar.signal.map {
      case None => div("Loading product data...")
      case Some(product) =>
        div(
          display.flex,
         // width := "20%",    //flex
          justifyContent.center,
          alignItems.center,
          minHeight := "100vh",
          flexDirection := "column",
          //boxSizing.borderBox,
         // border := "1px solid #ddd",
         // padding := "2px",
         // textAlign.center,

          //содержание карточки
         // href := router.absoluteUrlForPage(PageItem(item.category, item.itemCode)),//item.srcOut,
          //target := "_blank",
          img(
           // src := item.srcImg,
            src := product.srcImg,
            alt := "WEAR",
            width :=  "200%",
            height.auto,
            borderRadius := "10px"
          ),
          p(
            //item.name,
            product.name,
            fontSize := "24px",
            fontWeight.bold,
            textAlign.left
          ),
          p(
            //s"Articul: ${item.itemCode}",
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
    // }
   }


   // a(
    //  href := router.absoluteUrlForPage(PageCategory("Simon")),
    //  onClick.preventDefault --> (_ => router.pushState(PageCategory("Simon"))),
    //  "Press For Page-Category -Simon-"
    //)
  )
end renderItemPage

