package org.libss.lift.form

import net.liftweb.http.js.JE.{JsFunc, JsNot, JsVar, Str}
import net.liftweb.http.js.JsCmds.{Alert, JsCrVar, JsIf, _}
import net.liftweb.http.js.jquery.JqJE.{Jq, JqId}
import net.liftweb.http.js.{JsCmd, JsCmds}
import net.liftweb.http.{S, SHtml}
import org.libss.lift.boot.LibssRules
import org.libss.lift.util.{HeadComponents, HeadComponentsLoadable, ResourcePathHelper, SystemJSHelper}

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 08.06.2016 at 23:03.
  *
  * Main trait to be provided via context with all the API needed to properly validate form fields
  */
trait FormValidation extends HeadComponentsLoadable {
  def showValidationError(fieldId: String, errorMessage: String): JsCmd

  def hideValidationError(fieldId: String): JsCmd

  def showFormValidationError(errorMessage: String): JsCmd

  def hideFormValidationError(errorMessage: String): JsCmd

  def initFormValidation(formId: String): Option[() => JsCmd]

  def onErrorsCmd(errors: Seq[FieldError]): Option[JsCmd]
}


case class FieldError(fieldId: String, errorMessage: String, onError: () => JsCmd = () => JsCmds.Noop)


class FormValidationEngine
  extends FormValidation
    with ResourcePathHelper
    with HeadComponentsLoadable
    with SystemJSHelper {

  /**
    * @return the map of key of pack of libraries to list of library resources to be loaded
    */
  override def headComponents: Map[String, List[String]] = HeadComponents.validationEngine(Option(S.locale).flatMap(l => Option(l.getLanguage)).getOrElse(LibssRules.defaultLanguage))

  def showValidationError(fieldId: String, errorMessage: String): JsCmd =
    JqId(fieldId) ~> JsFunc("validationEngine", "showPrompt", S ? errorMessage, "error", "topRight", true)

  def hideValidationError(fieldId: String): JsCmd =
    JqId(fieldId) ~> JsFunc("validationEngine", "hide")

  def initFormValidation(formId: String): Option[() => JsCmd] = Some(() => loadAndCall(headComponents, Jq("form") ~> JsFunc("validationEngine")))

  def onErrorsCmd(errors: scala.Seq[FieldError]): Option[JsCmd] =
    errors.map {
      case FieldError(fieldId, errorMessage, onError) =>
        showValidationError(fieldId, errorMessage) & onError()
    }.reduceLeftOption(_ & _)

  def hideFormValidationError(errorMessage: String): JsCmd = JsCmds.Noop

  def showFormValidationError(errorMessage: String): JsCmd = Alert(errorMessage)
}


class CustomBootstrapFormValidation extends FormValidation {

  /**
    * @return the map of key of pack of libraries to list of library resources to be loaded
    */
  override def headComponents: Map[String, List[String]] = Map.empty

  def showValidationError(fieldId: String, errorMessage: String): JsCmd =
    JsCrVar("formGroup", JqId(fieldId) ~> JsFunc("parents", "div.form-group")) &
      JsIf(JsNot(JsVar("formGroup") ~> JsFunc("hasClass", "has-errors")),
        JsVar("formGroup") ~> JsFunc("removeClass", "has-success") &
          JsVar("formGroup") ~> JsFunc("addClass", "has-error") &
          JqId(fieldId) ~> JsFunc("parent") ~> JsFunc("append", Str("<span id=\'errorBlock" + fieldId + "\' class=\"help-block\"></span>")) &
          JqId(fieldId) ~> JsFunc("attr", "aria-describedby", "errorBlock" + fieldId)
      ) &
      JqId("errorBlock" + fieldId) ~> JsFunc("text", S ? errorMessage)

  def hideValidationError(fieldId: String): JsCmd =
    SHtml.ajaxInvoke(() => {
      JsCrVar("formGroup", JqId(fieldId) ~> JsFunc("parents", "div.form-group")) &
        JsIf(JsVar("formGroup") ~> JsFunc("hasClass", "has-error"),
          JsVar("formGroup") ~> JsFunc("removeClass", "has-error") &
            JqId("errorBlock" + fieldId) ~> JsFunc("remove")
        )
    })._2.cmd

  def initFormValidation(formId: String) = None

  def onErrorsCmd(errors: scala.Seq[FieldError]): Option[JsCmd] =
    errors.map {
      case FieldError(fieldId, errorMessage, onError) =>
        showValidationError(fieldId, errorMessage) & onError()
    }.reduceLeftOption(_ & _)

  def hideFormValidationError(errorMessage: String): JsCmd = JsCmds.Noop

  def showFormValidationError(errorMessage: String): JsCmd = Alert(errorMessage)
}

//TODO: check whether it's ok
trait ValidationEngineSupport {
  protected def showValidationError(fieldId: String, errorMessage: String): JsCmd =
    JqId(fieldId) ~> JsFunc("validationEngine", "showPrompt", S ? errorMessage, "error", "topRight", true)

  protected def hideValidationError(controlId: String): JsCmd =
    JqId(controlId) ~> JsFunc("validationEngine", "hide")

  protected def enableValidationEngine: JsCmd = Jq("form") ~> JsFunc("validationEngine")

  protected def onErrorsActions(errors: scala.Seq[FieldError]): Option[JsCmd] =
    errors.map {
      case FieldError(fieldId, errorMessage, onError) =>
        showValidationError(fieldId, errorMessage) & onError()
    }.reduceLeftOption(_ & _)
}
