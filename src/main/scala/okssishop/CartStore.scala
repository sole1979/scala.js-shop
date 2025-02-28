package frontend

//import com.raquo.airstream.core.{Observer, Signal}
//import api.AuthApi
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
//import com.raquo.laminar.api._
import org.scalajs.dom
import org.scalajs.dom.window


//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future


object CartStore {

  import org.scalajs.dom
  import org.scalajs.dom.window


  val cartVar: Var[List[CartProduct]] = Var(loadCart())
  //val cartVar: Var[List[CartProduct]] = Var(List.empty)

  // load cart from localstorage
  private def loadCart(): List[CartProduct] = {
    Option(window.localStorage.getItem("cart"))
      .filter(_.nonEmpty)   // sequence is not empty
      .map(json => upickle.default.read[List[CartProduct]](json))
      .getOrElse(Nil)  // if data is missing, return empty List
    }

  // save cart to localStorage
 // private def saveCart(): Unit = {
 //   localStorage.setItem("cart", upickle.default.write(cartVar.now()))
 // }

  //auto refresh cart for change
  cartVar.signal.foreach { cart =>
    window.localStorage.setItem("cart", upickle.default.write(cart))
    }(unsafeWindowOwner)

  // add to Cart
  def addToCart(productSku: String, productPrice: BigDecimal): Unit = {
    cartVar.update { cart =>
      cart.find(_.sku == productSku) match {
        case Some(item) => cart.map(i => if (i.sku == productSku) i.copy(quantity =  i.quantity + 1) else i)
        case None => cart :+ CartProduct(productSku, productPrice, 1)
      }
    }
  }

  // remove from Cart
  def removeFromCart(productSku: String): Unit = {
    cartVar.update(_.filterNot(_.sku == productSku))
  }

  def updateQuantity(sku: String, newQuantity: Int): Unit = {
    cartVar.update { cart =>
      if (newQuantity > 0) {
        cart.map {
          case item if item.sku == sku => item.copy(quantity = newQuantity)
          case other => other
        }
      } else {
        cart.filterNot(_.sku == sku)
      }
    }
  }


}
