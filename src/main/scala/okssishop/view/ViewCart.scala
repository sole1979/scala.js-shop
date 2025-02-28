package frontend
import com.raquo.airstream.core.Signal

import scala.scalajs.js

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._
import org.scalajs.dom.html.Anchor

import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global
import com.raquo.laminar.api.features.unitArrows

//val subscription = modalVar.signal.foreach(v => println(s"ModalVar changed: $v"))(unsafeWindowOwner)
  
def ViewCart(): HtmlElement ={
  val productsVar = Var(List.empty[Product])

  modalVar.signal.foreach { isOpen =>
    if (isOpen) {
      val skus: List[String] = CartStore.cartVar.now().map(_.sku)
      HttpClient.fetchProductsBySku(skus).foreach(productsVar.set)
    }
  }(unsafeWindowOwner)

  val cartItemsSignal: Signal[List[(Product, Int)]] = 
    productsVar.signal.combineWith(CartStore.cartVar.signal).map { case (products, cart) =>
        cart.flatMap { cartItem =>
          products.find(_.sku == cartItem.sku).map { product =>
            (product, cartItem.quantity)
          }
        }
      }

  div(
    cls := "modal-overlay",
    cls.toggle("modal-hidden") <-- modalVar.signal.map(!_),
    onClick --> { _ => modalVar.set(false) },
      div(
        cls := "modal-content",
        onClick --> { e => e.stopPropagation() },
        button(
          cls := "close-button",
          onClick --> { _ => modalVar.set(false) },
          "\u00D7"
        ),
        h2(
          textAlign := "left",
          marginLeft := "15px",
          "Cart"
        ),
       //
       // child <-- cartItemsSignal.map { cartItems =>
         // if (cartItems.isEmpty) { div("Cart is empty") }
         // else {
          // div{

            table(
              width := "100%",
              borderCollapse := "collapse",
              tbody(
                children <-- cartItemsSignal.map { cartItems =>
                 if (cartItems.isEmpty) { List(tr(td(colSpan := 4, textAlign := "center", "Cart is empty"))) }
                 else {
                  cartItems.map { case (product, quantity) =>
                    tr(
                      td(
                        img(
                          src := product.srcImg,
                          height := "78px",
                          width := "auto"
                        )
                      ),
                      td(
                        div(//cls := "product-info",
                          textAlign := "left",
                          span(cls := "product-name", product.name),
                          br(),
                          span(cls := "product-price", s"${product.price} UAH")
                        )
                      ),
                      td(
                        div(cls := "quantity-control",
                           button("-", onClick --> { (e: dom.Event) =>
                             e.stopPropagation()
                             CartStore.updateQuantity(product.sku, quantity - 1)
                           }),
                           span(cls := "quantity", quantity.toString),
                           button("+", onClick --> { (e: dom.Event) =>
                             e.stopPropagation()
                             CartStore.updateQuantity(product.sku, quantity + 1)
                           })
                         )
                       ),
                       td(
                         span(/*cls := "total-price"*/fontWeight := "bold", s"${product.price * quantity} UAH")
                       )
                     )
                   }
                 }
                }
               )
             ),
             div(
               textAlign := "right",
               marginRight := "10px",
               marginTop := "20px",
               fontSize := "18px",
               fontWeight := "bold",
               span("Total: "),
               span(
                 child.text <-- cartItemsSignal.map { cartItems =>
                   cartItems.map { case (product, quantity) => product.price * quantity }.sum.toString + " UAH"
                 }
               )
             )
           )

      //    }
      //  }
     // }
  )
}

