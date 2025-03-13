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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext

//import upickle.default.ReadWriter
//import upickle.default._
//import urldsl.vocabulary.UrlMatching
//import upickle.default.{ReadWriter => RW, macroRW}

//implicit val ec: ExecutionContext = ExecutionContext.global

def renderItemPage(category: String, itemCode: String): HtmlElement =

  productVar.set(None) 
  HttpClient.fetchProduct(category, itemCode)

  def renderAddToCartButton(sku: String, price: BigDecimal): HtmlElement = {
    val isInCart: Signal[Boolean] = CartStore.cartVar.signal.map(_.exists(_.sku == sku))
    button(
      padding := "10px 15px",
      child <-- isInCart.map(if (_) "In Cart" else "Add to Cart"),
      backgroundColor <-- isInCart.map(if (_) "grey" else "black"),
      color <-- isInCart.map(if (_) "black" else "white"),
      disabled <-- isInCart,
      onClick --> { _ =>
        CartStore.cartVar.update { cart =>
          CartProduct(sku, price, 1) :: cart
        }
      }
    )
  }

  def renderFavoriteButton(sku: String): HtmlElement = {
    val isFavorite: Signal[Boolean] = favoritesVar.signal.map(_.contains(sku))//_.exists(_.sku == sku))

    button(
      padding := "10px 15px",
      child <-- isFavorite.map(if (_) "Remove from Favorites" else "Add to Favorites"),
      backgroundColor <-- isFavorite.map(if (_) "red" else "white"),
      color <-- isFavorite.map(if (_) "white" else "black"),
      borderColor := "red",
      borderWidth := "2px",
      borderStyle := "solid",
      onClick.mapTo(()) --> (_ => UserSession.toggleFavorite(sku))   //toggleFavoriteObserver
    )
  }

  div(
     child <-- productVar.signal.map {
      case None => div("Loading product data...")
      case Some(product) =>
       div(
        display.flex,
        alignItems.center,
        gap := "20px",
        div(
          flex := "0 0 auto",
          img(
            src := product.srcImg,
            alt := "WEAR",
            width :=  "400px",
            height.auto,
            borderRadius := "10px"
          )
        ),

        div(
          flex := "1",
          display.flex,
          flexDirection.column,
          gap := "10px",
          div(
           display.flex,
           gap := "5px",
           a(
            href := router.absoluteUrlForPage(PageHome),
            "Main > "
          ),
          a(
            href := router.absoluteUrlForPage(PageCategory(s"$category")),
            s"$category > "
          ),
          span(s"$itemCode")
         ),

          p(
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
          ),
         br(),
         renderAddToCartButton(product.sku, product.price),
         br(),
         renderFavoriteButton(product.sku)
        )
       )
   }


  )
end renderItemPage

