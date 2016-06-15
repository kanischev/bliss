package org.libss.lift.security

import com.google.inject.Inject
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.js.JsCmds.FocusOnLoad
import net.liftweb.http.{S, SHtml, SessionVar}
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._
import org.libss.lift.security.providers.ExternalOAuthHandler
import org.libss.logic.guice.Injection
import org.libss.logic.security.UserService
import org.libss.util.helpers.UrlHelper
import scala.collection.JavaConversions._
import scala.xml.{Elem, NodeSeq, Text}

/**
  * Created by Kaa 
  * on 12.06.2016 at 20:53.
  */
object LoginErrorHolder extends SessionVar[Box[String]](Empty) {
  override protected def __nameSalt = Helpers.nextFuncName
}

trait LogInRedirectHelper {
  def onLoginRedirect = "/"
  def alwaysRedirectToHomeOnLogin = false

  def getRedirectToUrl = {
    val redir = LoginRedirect.is match {
      case Full(url) =>
        LoginRedirect(Empty)
        url
      case _ =>
        onLoginRedirect
    }
    if (alwaysRedirectToHomeOnLogin)
      onLoginRedirect
    else
      redir
  }
}

// TODO: absolutely awful
trait LoginForm
  extends Injection
    with UrlHelper
    with LogInRedirectHelper {

  @Inject
  protected var userService: UserService[_] = _
  @Inject(optional = true)
  protected var oauthHandler: ExternalOAuthHandler = _

  protected def registerLink: Option[String] = None

  protected def registerLabel: Option[String] = None

  protected def loginFormTemplate: NodeSeq = {
    (
      <div id="login_screen" align="center">
        <div id="login-widget">
          <user:external/>
          <form method="post" action={prefixWithSplitterIfNeeded(loginPageUrl)}>
            <fieldset>
              <user:loginError/>
              <user:usernameLabel/>
              <user:username/>
              <user:userpasswordLabel/>
              <user:userpassword/>
              <span style="display: block;">
                <div style="display: inline; float: left;" class="rememberPasswordLink">
                  <user:forgotPassword/>
                  <user:register/>
                </div>
                <div style="display: inline; float: right;">
                  <user:submit/>
                </div>
              </span>
            </fieldset>
          </form>
        </div>
      </div>
      )
  }

  protected def fieldLabel(labelText: String, labelField: String) =
    <label for={labelField}>{labelText}</label>

  protected def submitButton = {
    SHtml.submit("Войти", () => {}, "name" -> "submit", "style" -> "margin-top: 10px; width: auto; height: auto;",  "class" -> "btn btn-warning")
  }

  protected def renderLoginError(errorText: String): NodeSeq = Text(errorText)

  protected def forgotPasswordUrl: String
  protected def loginPageUrl: String

  protected def renderForgotPassword = {
    <a href={prefixWithSplitterIfNeeded(forgotPasswordUrl)}>{Text("Напомнить пароль")}</a>
  }

  protected def renderLoggedIn: NodeSeq = <div>Вы уже авторизованы</div>

  /**
    * Extension point
    *
    * @param elem single external oauth link
    * @return wrapped external oauth link
    */
  protected def wrapSingleAuthHref(elem: Elem) = elem

  protected def renderExternalAuths: NodeSeq = {
    if (oauthHandler != null)
      oauthHandler.oauthClients.toSeq.flatMap(cl => wrapSingleAuthHref(cl.renderClickButton))
    else NodeSeq.Empty
  }

  protected def loginFieldLabel = "Логин"

  protected def loginForm = {
    val ns =
      bind("user", loginFormTemplate,
        "external" -> renderExternalAuths,
        "loginError" -> LoginErrorHolder.get.map(le => renderLoginError(le)).getOrElse(NodeSeq.Empty),
        "usernameLabel" -> fieldLabel("Логин", "username"),
        "username" -> (FocusOnLoad(<input type="text" name="username" id="username" value={userService.getUserLogin.orElse(S.param("username").toOption).getOrElse("")}/>)),
        "userpasswordLabel" -> fieldLabel("Пароль", "userpassword"),
        "userpassword" -> <input type="password" name="userpassword" id="userpassword"/>,
        "forgotPassword" -> {if (LoginErrorHolder.get.isDefined) renderForgotPassword else NodeSeq.Empty},
        "register" -> registerLink.map(rl => <a href={rl}>{registerLabel.getOrElse("Зарегистрироваться")}</a>).getOrElse(NodeSeq.Empty),
        "submit" -> submitButton)
    LoginErrorHolder.get.foreach(_ => LoginErrorHolder.set(Empty))
    ns
  }

  def render(ns: NodeSeq) = {
    if (S.post_?) {
      val userValidationError = userService.logUserIn(S.param("username").getOrElse(""), S.param("userpassword").getOrElse(""))

      userValidationError match {
        case None if userService.isLoggedIn => {
          LoginErrorHolder.set(Empty)
          S.redirectTo(getRedirectToUrl)
        }

        case _ => {
          LoginErrorHolder.set(Box(userValidationError.map(_.message).getOrElse("Возникла ошибка авторизации. Обратитесь к Администрации.")))
          loginForm
        }
      }
    } else {
      if (userService.isLoggedIn)
        renderLoggedIn
      else
        loginForm
    }
  }
}