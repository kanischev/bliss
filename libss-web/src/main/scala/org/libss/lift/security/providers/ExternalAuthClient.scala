package org.libss.lift.security.providers

import com.google.inject.{Inject, Provider}
import net.liftweb.common.{Empty, Full}
import net.liftweb.http._
import net.liftweb.http.js.JsCmds.Script
import net.liftweb.http.js.{JsCmd, JsCmds}
import org.libss.lift.boot.LibssLocalizable
import org.libss.lift.security.LoginErrorHolder
import org.libss.lift.util.{JsWindowOpen, ResourcePathHelper}
import org.libss.logic.i18n.InjectedLocalizable
import org.libss.logic.security.User

import scala.collection.JavaConversions._
import scala.xml.{Elem, NodeSeq}

/**
 * date: 12.06.16
 * author: Kaa
 */
object ProviderIconSize {
  val Small = "small"
  val Normal = "normal"
}


trait ExternalOAuthHandler {
  @Inject
  var oauthClientsProvider: Provider[java.util.Set[ExternalAuthClient]] = _

  lazy val oauthClients = oauthClientsProvider.get()

  lazy val oauthClientsMap = oauthClients.map(cl => cl.uniqueName -> cl).toMap

  def defaultExternalAuthErrorMessage: String = ""

  def matcher: LiftRules.DispatchPF = {
    case req @ Req("external_auth" :: authClientName :: "authenticate" :: Nil, _, GetRequest) if oauthClientsMap.contains(authClientName) =>
      () => signUpRedirect(oauthClientsMap(authClientName), req)
    case req @ Req("external_auth" :: authClientName :: "callback" :: Nil, _, GetRequest) if oauthClientsMap.contains(authClientName) =>
      () => {
        val ns: NodeSeq = Script(processCallBack(oauthClientsMap(authClientName), req))
        Full(XhtmlResponse(ns(0), Empty, Nil, Nil, 200, renderInIEMode = false))//())
      }
  }

  def signUpRedirect(client: ExternalAuthClient, req: Req) = {
    S.redirectTo(client.buildLoginRedirectUrl)
  }

  def onSuccessfulAuthWithoutEmail(client: ExternalAuthClient, user: User): JsCmd

  def onSuccessfulAuth(client: ExternalAuthClient, user: User): JsCmd

  def onFailureAuth(client: ExternalAuthClient, req: Req): JsCmd = {
    LoginErrorHolder.set(Full(defaultExternalAuthErrorMessage.format(client.uniqueName.capitalize)))
    JsCmds.Noop
  }

  def processCallBack(client: ExternalAuthClient, req: Req): JsCmd = {
    val userOpt = client.handleCallback(req)
    if (userOpt.isDefined && client.hasEmailInfo)
      onSuccessfulAuth(client, userOpt.get)
    else if (userOpt.isDefined)
      onSuccessfulAuthWithoutEmail(client, userOpt.get)
    else
      onFailureAuth(client, req)
  }
}

trait ExternalAuthClient extends ResourcePathHelper with LibssLocalizable {
  abstract override def resourceNames: List[String] = "i18n.org.libss.lift.security.auth" :: super.resourceNames

  def hasEmailInfo: Boolean = true
  def uniqueName: String
  def buildLoginRedirectUrl: String
  def handleCallback(req: Req): Option[User]
  def imageSize = ProviderIconSize.Normal

  def iconResource = inClassPath({
    imageSize match {
        case ProviderIconSize.Small => "/img/auth_provider/small/"
        case _ => "/img/auth_provider/"
      }
    } + uniqueName + ".png"
  )

  def callbackUrl = S.hostAndPath + "/external_auth/" + uniqueName + "/callback"

  def providerIcon = <img src={iconResource} alt={uniqueName}/>

  def renderClickButton: Elem =
    SHtml.a(providerIcon,
      JsWindowOpen("/external_auth/"+uniqueName+"/authenticate",
                     i18n("auth.external.window.title").format(uniqueName.capitalize),
                     Map("width" -> "550",
                         "height" -> "450",
                         "toolbar" -> "no",
                         "location" -> "no")
      )
    )
}

case class AuthTokenResponse(access_token: String, token_type: String)