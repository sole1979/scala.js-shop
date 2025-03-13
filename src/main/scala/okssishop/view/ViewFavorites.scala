package frontend

import com.raquo.airstream.core.Signal
import com.raquo.waypoint._
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._

import org.scalajs.dom

import org.scalajs.dom.html.Anchor
import scala.concurrent.ExecutionContext.Implicits.global
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.MouseEvent
import scala.util.{Success, Failure}



def renderFavoritesPage(): HtmlElement =
  UserSession.loadFavorites()

  favoritesVar.signal.foreach { _ =>
    UserSession.refreshFavoritesProductsVar()
  }(unsafeWindowOwner)

  div(
    br(),
    h2(s"Favorites Page"),
    a(
      href := router.absoluteUrlForPage(PageHome),
     "Home!"
    ),

   div(
    display.flex,
    flexWrap.wrap,
    justifyContent.flexStart,
    gap("10px"),
    children <-- favoritesProductsVar.signal.map { products =>
      products.map { product =>
        div(
          position.relative,
          flex := "1 1 calc(25% - 20px)",
          boxSizing.borderBox,
          border := "1px solid #ddd",
          padding := "2px",
          textAlign.center,

          a(
            display.block,
            href := router.absoluteUrlForPage(PageItem(product.category, product.sku)),
         //   styleAttr := "pointer-event:none",

            img(
              src := product.srcImg,
              maxWidth :=  "100%",
              height := "auto",
              borderRadius := "10px"
            ),

            img(
              src := "/img/system/icon_garbage5.png",
              alt := "Delete",
              width := "40px",
              height := "40px",
              position.absolute,
              top := "5px",
              right := "5px",
              cursor.pointer,
              opacity := "0.7",
              onClick --> { (e: dom.Event) =>
                e.preventDefault()
                e.stopPropagation()
                UserSession.removeFavorite(product.sku)
             //   HttpClient.deleteFromFavorite(product.sku).foreach {
            //      case Right(favorites) => UserSession.favoritesVar.set(favorites)
             //     case Left(error) => println(error)
             //   }
              }
            ),
            div(
              product.name,
              fontSize := "20px",
              fontWeight.bold,
              textAlign.left
            ),
            p(s"Price: ${product.price}",
              textAlign.left
            )
          )
        )
      }
    }
   )


  )

end renderFavoritesPage

