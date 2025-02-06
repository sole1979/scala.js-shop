package frontend

import com.raquo.airstream.core.Signal
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._
import org.scalajs.dom
//import org.scalajs.dom.html.Anchor

//import org.scalajs.dom
import Main._

//case class LoginFormState(email: String, password: String)



val loginSubmitter = Observer[Unit] { _ =>
  showErrorsVar.set(true)
  loginError.set(None)

  if (!isEmailErrorVar.now() && !isPasswordErrorVar.now()) {
    val loginFormState = LoginFormState(emailVar.now(), passwordVar.now())
    HttpClient.login(loginFormState)
    //dom.window.alert(s"Submitted form: $loginFormState")
  }
}

def renderLoginMenu(): Div =
    nameVar.set("")
    emailVar.set("")
    passwordVar.set("")
    showErrorsVar.set(false)
    isNameErrorVar.set(false)
    isEmailErrorVar.set(false)
    isPasswordErrorVar.set(false)
    loginError.set(None)


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
          //onClick.mapTo(false) --> isLeftMenuVisible
          onClick.mapTo("main") --> currentLeftMenuVar
        ),
        div(
          span(
            "Login",
            color := "black",
            fontSize := "24px",
            lineHeight := "1.5",
            textDecoration := "underline",
            marginLeft := "50px"
          ),
          button(
            "Register",
            color := "gray",
            fontSize := "24px",
            lineHeight := "1.5",
            marginLeft := "20px",
            padding := "0",
            border := "none",
            background := "none",
            cursor := "pointer",
            onClick.mapTo("auth") --> currentLeftMenuVar
          ),
        )
      ),
      form(
         onSubmit.preventDefault.mapTo(()) --> loginSubmitter,

        display.flex, 
        flexDirection.column,
        br(),
        input(
          `type` := "text",
          placeholder := "Email",
          marginBottom := "10px",
          controlled(
            value <--  emailVar.signal,
            onInput.mapToValue --> emailVar
          )
        ),
        child <-- errorEmailVar.combineWith(showErrorsVar.signal).map {
          case (Some(error), true) => div(color := "red", error)
          case _ => emptyNode
        },
      //  br(),
        input(
          `type` := "text",
          placeholder := "Password",
          marginBottom := "10px",
          controlled(
            value <--  passwordVar.signal,
            onInput.mapToValue --> passwordVar
          )
        ),
        child <-- errorPasswordVar.combineWith(showErrorsVar.signal).map {
          case (Some(error), true) => div(color := "red", error)
          case _ => emptyNode
        },
        button(
          "Login",
          `type` := "submit",
          marginTop := "5px",
          padding := "5px 10px",
         // display.block
        ),
        child.maybe <-- loginError.signal.map(_.map { errorMsg =>
          div(color := "red",  errorMsg)
        }),
        child.maybe <-- userVar.signal.map(_.map { user =>
          div(color := "green",  s"Welcome, ${user.name}")
        })
      )
    )



