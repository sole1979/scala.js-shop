package frontend

import com.raquo.airstream.core.Signal
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api._
import org.scalajs.dom
//import org.scalajs.dom.html.Anchor

//import org.scalajs.dom
import Main._

//case class AuthFormState(name: String, email: String, password: String)
val showErrorsVar = Var(false)
val loginError = Var(Option.empty[String])
val registerError = Var(Option.empty[String])

val nameVar = Var("")
val emailVar = Var("")
val passwordVar = Var("")

val isNameErrorVar: Var[Boolean] = Var(false)
val isEmailErrorVar: Var[Boolean] = Var(false)
val isPasswordErrorVar: Var[Boolean] = Var(false)

val errorNameVar: Signal[Option[String]]  = nameVar.signal.map { name =>
  val trimmedName = name.trim
  val nameRegex = "^[a-zA-Zà-ÿÀ-ß]+([ '-][a-zA-Zà-ÿÀ-ß]+)*$"

  if (trimmedName.length < 3 || trimmedName.length > 50) {
    isNameErrorVar.set(true)
    Some("Name must be 3-50 characters")
  } else if (!trimmedName.matches(nameRegex))  {
    isNameErrorVar.set(true)
    Some("Name contains invalid characters")
  } else {
  isNameErrorVar.set(false)
  None
  }
}

val errorEmailVar: Signal[Option[String]]  = emailVar.signal.map { email =>
  if (!email.nonEmpty) {
    isEmailErrorVar.set(true)
    Some("Email is empty!")
  } else if (!email.contains("@"))  {
    isEmailErrorVar.set(true)
    Some("Email dones not contain '@'")
  } else if (email.contains(" "))  {
    isEmailErrorVar.set(true)
    Some("Please remove extra spaces")
  } else {
  isEmailErrorVar.set(false)
  None
  }
}

val errorPasswordVar: Signal[Option[String]]  = passwordVar.signal.map { password =>
  val invalidChars = Set(' ', '?', '=', '&', '<', '>', '|', '\'', '\\', '"', ';', '{', '}', '^', '~')

  if (password.exists(invalidChars.contains)) {
    isPasswordErrorVar.set(true)
    Some("Password contains invalid characters")
  } else if (password.length < 8 || password.length > 64) {
    isPasswordErrorVar.set(true)
    Some("Password must be 8-64 characters")
  } else {
    isPasswordErrorVar.set(false)
    None
  }
}

val RegisterSubmitter = Observer[Unit] { _ =>
  showErrorsVar.set(true)
  registerError.set(None)

  if (!isNameErrorVar.now() && !isEmailErrorVar.now() && !isPasswordErrorVar.now()) //showErrorsVar.set(true)
 // else {
    {val registerFormState = RegisterFormState(nameVar.now(), emailVar.now(), passwordVar.now())
     HttpClient.register(registerFormState)
    //dom.window.alert(s"Submitted form: $authFormState")
  }
}

def renderAuthMenu(): Div =
    nameVar.set("")
    emailVar.set("")
    passwordVar.set("")
    showErrorsVar.set(false)
    isNameErrorVar.set(false)
    isEmailErrorVar.set(false)
    isPasswordErrorVar.set(false)
    registerError.set(None)

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
          button(
            "Login",
            color := "gray",
            fontSize := "24px",
            lineHeight := "1.5",
            padding := "0",
            border := "none",
            background := "none",
            cursor := "pointer",
            marginLeft := "50px",
            onClick.mapTo("login") --> currentLeftMenuVar
          ),
          span(
            "Register",
            color := "black",
            fontSize := "24px",
            lineHeight := "1.5",
            textDecoration := "underline",
            marginLeft := "20px"
          ),
        )
      ),
      form(
         onSubmit.preventDefault.mapTo(()) --> RegisterSubmitter,

        display.flex, 
        flexDirection.column,
        br(),
        input(
          `type` := "text",
          placeholder := "UserName",
          marginBottom := "10px",
          controlled(
            value <--  nameVar.signal,
            onInput.mapToValue --> nameVar
          )
        ),
        child <-- errorNameVar.combineWith(showErrorsVar.signal).map {
          case (Some(error), true) => div(color := "red", error)
          case _ => emptyNode
        },
       // br(),
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
          "Register",
          `type` := "submit",
          marginTop := "5px",
          padding := "5px 10px",
         // display.block
        ),
        child.maybe <-- registerError.signal.map(_.map { errorMsg =>
          div(color := "red",  errorMsg)
        }),
        child.maybe <-- userVar.signal.map(_.map { user =>
          div(color := "green",  s"Welcome, ${user.name}")
        })
      )
    )



