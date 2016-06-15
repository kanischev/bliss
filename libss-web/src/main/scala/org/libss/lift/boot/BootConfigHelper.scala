package org.libss.lift.boot

import java.util.Locale
import javax.naming.InitialContext
import javax.sql.DataSource

import com.google.inject.{AbstractModule, Inject}
import net.liftweb.common.{Box, Full}
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.http._
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.util.{LiftFlowOfControlException, LoanWrapper, Mailer}
import org.libss.lift.LibssArtifacts
import org.libss.lift.util.LibssLocaleVar
import org.libss.logic.guice.{InjectionConfigurator, InjectorHolder}
import org.libss.logic.liquibase.DataSourceUpdater
import org.libss.logic.security.UserService
import org.libss.logic.squeryl.SquerylHelper
import org.libss.util.Loggable
import org.squeryl.{PrimitiveTypeMode, SessionFactory}

/**
  * Created by Kaa 
  * on 09.06.2016 at 00:12.
  */
trait BootConfigHelper
  extends InjectionConfigurator
  with SquerylHelper
  with PrimitiveTypeMode
  with Loggable {

  @Inject(optional = true)
  protected var dsUpdater: DataSourceUpdater = _
/*
  @Inject
  protected var siteMapGenerator: SiteMapGenerator = _
*/
  @Inject(optional = true)
  protected var userService: UserService[_] = _
/*
  @Inject(optional = true)
  protected var activityHandler: SessionActivityHandler = _
  @Inject(optional = true)
  protected var externalOAuth: ExternalOAuthHandler = _
  @Inject(optional = true)
  protected var paymentProvidersHandler: ExternalPaymentProviderCallbackHandler = _
*/

  def modules: Seq[_ <: AbstractModule]

  def dsMap: Map[String, String]

  def boot() {
    installAll(modules)
    val dataSources = dsMap.map {
      case (dsName, dsJndi) => dsName -> InitialContext.doLookup[DataSource](dsJndi)
    }
    InjectorHolder.injectorSafeGet.injectMembers(this)
    if (dsUpdater != null) dsUpdater.update(dataSources.toSeq)

    setup()
  }

  def squerylWrapperSetup() {
    S.addAround(new LoanWrapper {
      override def apply[T](f: => T): T = {
        val resultOrExcept = inTransaction {
          try {
            Right(f)
          } catch {
            case e: LiftFlowOfControlException => Left(e)
          }
        }

        resultOrExcept match {
          case Right(result) => result
          case Left(except) => throw except
        }
      }
    })
  }

  // To be implemented in descendants
  def setupRules()

  protected def setup() {
    setupCommonRules()
    setupRules()
  }

  protected def squerylSessionFactory() = {
    createSession(dsMap("default"))
  }

  protected def setupSqueryl() {
    if (dsMap.nonEmpty) {
      SessionFactory.concreteFactory = Some(squerylSessionFactory _)
      squerylWrapperSetup()
    }
  }

  protected def setupArtifactRewriter() {
    LiftRules.jsArtifacts = LibssArtifacts
  }

  protected def setupCommonRules() {
    setupArtifactRewriter()
    setupExceptionHandler()
    LiftRules.ajaxPostTimeout = 40000
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    //LiftRules.setSiteMapFunc(siteMapGenerator.siteMap _)

    LiftRules.addToPackages("org.libss.lift.components")

    setupLocalization()
    setupSqueryl()
    setupResourceServer()
    setupLogout()
    addExplicitSnippetBindings()
    setupPaymentProvidersCallbackHandler()
    setupExternalOAuth()
  }

  protected def showException(le: Throwable): String = {
    val ret = "Message: " + le.toString + "\n\t" +
      le.getStackTrace.map(_.toString).mkString("\n\t") + "\n"

    val also = le.getCause match {
      case null => ""
      case sub: Throwable => "\nCaught and thrown by:\n" + showException(sub)
    }

    ret + also
  }

  protected def setupExceptionHandler() {
    LiftRules.exceptionHandler.prepend {
      case (_, r, e) =>
        Logger.error("Exception being returned to browser when processing " + r.uri.toString + ": " + showException(e))
        if (S.request.open_!.uri.startsWith("/ajax_request")) {
          JavaScriptResponse(Alert(e match {
            case _ => e.toString
          }))
        }
        else XhtmlResponse((<html> <body>Exception occured while processing {r.uri}<pre>{showException(e)}</pre> </body> </html>), S.htmlProperties.docType, List("Content-Type" -> "text/html; charset=utf-8"), Nil, 500, S.ieMode)
    }
  }

  protected def setupLogout() {
    LiftRules.dispatch.append {
      case Req("logout" :: Nil, _, GetRequest) =>
        S.session.foreach(_.destroySession())
        S.redirectTo("/")
    }
  }

  protected def setupExternalOAuth() {
/*
    if (externalOAuth != null)
      LiftRules.dispatch.append(externalOAuth.matcher)
*/
  }

  protected def setupPaymentProvidersCallbackHandler() {
/*
    if (paymentProvidersHandler != null)
      LiftRules.dispatch.append(paymentProvidersHandler.matcher)
*/
  }


  protected def configMailer(jndiName: String) {
    Mailer.jndiName = Full(jndiName)
  }

  protected def setupResourceServer() {
    ResourceServer.allow {case _ => true}
  }

  protected def addExplicitSnippetBindings() {
/*
    LiftRules.snippetDispatch.append {
      case "has_role" => new HasRole
      case "user_login" => new UserLogin
    }
*/
  }

  protected def defaultLocale = Locale.ROOT

  var localizedSetup = false
  val LOCALE_COOKIE_NAME = "SelectedLocale"

  // Properly convert a language tag to a Locale
  def computeLocale(tag : String) = tag.split(Array('-', '_')) match {
    case Array(lang) => new Locale(lang)
    case Array(lang, country) => new Locale(lang, country)
    case Array(lang, country, variant) => new Locale(lang, country, variant)
  }

  /**
    * Setting up Lift localization rules. Change @link defaultLocale if you need
    * Also adds filling of LibssLocaleVar SessionVar when it's empty for proper localization handling in comet snippets
    */
  protected def setupLocalization() = {
    localizedSetup = true
    // Define this to be whatever name you want

    LiftRules.localeCalculator = {
      case fullReq @ Full(req) =>
        // Check against a set cookie, or the locale sent in the request
        def currentLocale = {
          S.findCookie(LOCALE_COOKIE_NAME).flatMap(
            cookie => cookie.value.map(computeLocale)
          ).openOr(LiftRules.defaultLocaleCalculator(fullReq))
        }
        // Check to see if the user explicitly requests a new locale
        S.param("locale") match {
          case Full(requestedLocale) if requestedLocale != null =>
            val computedLocale = computeLocale(requestedLocale)
            S.addCookie(HTTPCookie(LOCALE_COOKIE_NAME, requestedLocale))
            LibssLocaleVar.set(Box.legacyNullTest(computedLocale))
            computedLocale
          case _ => if (LibssLocaleVar.set_?) LibssLocaleVar.get.openOr(currentLocale) else currentLocale
        }

      case _ => defaultLocale
    }
  }

  protected def configureSessionsMonitoring() {
/*
    LiftSession.afterSessionCreate = List(activityHandler.onSessionCreation _)
    LiftSession.onShutdownSession  = List(activityHandler.onSessionShutdown _)
    LiftSession.onBeginServicing   = List(activityHandler.onBeginServicing _)
    SessionMaster.sessionWatchers = SessionWatcherServer :: SessionMaster.sessionWatchers
*/
  }

  /********************** AJAX FILE UPLOAD *****************************/
  // In cases where we have an AJAX request for IE with an uploaded file, we
  // assume we served through an iframe (a fairly safe assumption) and serve
  // up the response with a content type of text/plain so that IE does not
  // attempt to save the response as a downloaded file.
  LiftRules.responseTransformers.append {
    resp =>
      (for (req <- S.request) yield {
        resp match {
          case InMemoryResponse(data, headers, cookies, code)
            if req.uploadedFiles.nonEmpty &&
              req.isIE &&
              req.path.wholePath.head == LiftRules.ajaxPath =>
            val contentlessHeaders = headers.filterNot(_._1.toLowerCase == "content-type")
            InMemoryResponse(data, ("Content-Type", "text/plain; charset=utf-8") :: contentlessHeaders, cookies, code)
          case _ => resp
        }
      }) openOr resp
  }

  /********************************************************************/

}
