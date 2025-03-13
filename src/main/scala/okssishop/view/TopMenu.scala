package frontend

import com.raquo.airstream.core.Signal
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._
import org.scalajs.dom
//import org.scalajs.dom.html.Anchor

//import org.scalajs.dom
import Main._
import UserSession.currentUserVar


val modalVar = Var(false)
//val totalQuantityFavorites = favoritesVar.signal.map(_.size)
val isFavoritesEmpty: Signal[Boolean] = favoritesVar.signal.map(_.isEmpty)  

//modalVar.signal.foreach(v =>
//  println(s"ModalVar changed: $v"))(unsafeWindoOwner)

private def renderTopMenu(): Div =
  //val totalQuantity = UserSession.favoritesVar.signal.map(_.size)
  //currentUserVar
 // UserSession.loadFavorites()

  

  def renderCartTotal(): HtmlElement = {
    val totalPrice = CartStore.cartVar.signal.map(_.map(p => p.price * p.quantity).sum)
    div(
      child.text <-- totalPrice.map(t => f"$t%.2f")
    )
  }

  def renderCartQuantity(): HtmlElement = {
    val totalQuantity = CartStore.cartVar.signal.map(_.map(_.quantity).sum)
    div(
      position.absolute,
      top := "25px",
      right := "-5px",
      backgroundColor := "red",
      color := "white",
      fontSize := "12px",
      fontWeight.bold,
      borderRadius := "50%",
      width := "18px",
      height := "18px",
      textAlign.center,
      lineHeight := "18px",
      display <-- totalQuantity.map(q => if (q == 0) "none" else "block"),
      child.text <-- totalQuantity.map(_.toString)// {
    )
  }

  def cartButton() = div(
    position.relative,
    button(
      img(
        src := "/img/system/cart4.svg",
        alt := "Cart",
        width := "34px",
        height := "34px"
      ),
      onClick --> {_ => modalVar.set(true)},
    ),
    renderCartQuantity(),
  )

  def renderFavoriteQuantity(): HtmlElement = {
    //val totalQuantity = UserSession.favoritesVar.signal.map(_.size)
    val totalQuantityFavorites = favoritesVar.signal.map(_.size)
    div(
      position.absolute,
      top := "25px",
      right := "-5px",
      backgroundColor := "red",
      color := "white",
      fontSize := "12px",
      fontWeight.bold,
      borderRadius := "50%",
      width := "18px",
      height := "18px",
      textAlign.center,
      lineHeight := "18px",
      display <-- totalQuantityFavorites.map(q => if (q == 0) "none" else "block"),
      child.text <-- totalQuantityFavorites.map(_.toString)// {
    )
  }

  def favoriteButton(): HtmlElement = {
  //  val isFavoritesEmpty: Signal[Boolean] = UserSession.favoritesVar.signal.map(_.isEmpty)

    val favoriteImage = isFavoritesEmpty.map {
      case false => "/img/system/heart-red-icon.svg"
      case true => "/img/system/heart-thin-icon.svg"
    }
    div(
      position.relative,
      button(
        img(
          src <-- favoriteImage,
          alt := "Favorite",
          width := "34px",
          height := "34px"
        ),
        onClick --> {_ => router.pushState(PageFavorites)}
      ),
      renderFavoriteQuantity()
    )

  }

    div(
      position.fixed,
      top := "0",
      left := "0",
      width := "100%",
      backgroundColor := "#333",// "#d3d3d3"
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
      div(
        display := "flex",
        justifyContent := "space-between",
        width := "100%",
        gap := "20px",
      child <-- currentUserVar.signal.map {    //(_.map { user =>
        case Some(user) =>
          div(color := "green", marginLeft := "50px", s"${user.name}", flex := "1", textAlign := "right")
        case None =>
          div(color := "green", marginLeft := "50px", "Guest", flex := "1", textAlign := "right")
      },
      div(
        favoriteButton()
      ),
      div(
        cartButton()
      ),
        div(
          "My Order",
          renderCartTotal(),
          marginRight := "20px"
        )
      )
    )
