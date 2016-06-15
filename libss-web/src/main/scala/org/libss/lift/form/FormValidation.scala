package org.libss.lift.form

import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JE.{JsFunc, JsNot, JsVar, Str}
import net.liftweb.http.js.JsCmds.{Alert, JsCrVar, JsIf}
import net.liftweb.http.js.jquery.JqJE.{Jq, JqId}
import net.liftweb.http.js.{JsCmd, JsCmds}
import org.libss.lift.util.ResourcePathHelper
import JsCmds._

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 08.06.2016 at 23:03.
  *
  * Main trait to be provided via context with all the API needed to properly validate form fields
  */
trait FormValidation {
  def headAddition: NodeSeq

  def showValidationError(fieldId: String, errorMessage: String): JsCmd

  def hideValidationError(fieldId: String): JsCmd

  def showFormValidationError(errorMessage: String): JsCmd

  def hideFormValidationError(errorMessage: String): JsCmd

  def initFormValidation(formId: String): JsCmd

  def onErrorsCmd(errors: Seq[FieldError]): Option[JsCmd]
}


case class FieldError(fieldId: String, errorMessage: String, onError: () => JsCmd = () => JsCmds.Noop)


class FormValidationEngine extends FormValidation with ResourcePathHelper {
  override def headAddition: NodeSeq = {
    <script src={inClassPath("/js/jquery/jquery.validationEngine-ru.js")} type="text/javascript" charset="utf-8">
    </script> ++
      <script src={inClassPath("/js/jquery/jquery.validationEngine.js")} type="text/javascript" charset="utf-8">
      </script> ++
        <link rel="stylesheet" href={inClassPath("/css/validation/validationEngine.jquery.css")} type="text/css"/>
  }

  def showValidationError(fieldId: String, errorMessage: String): JsCmd =
    JqId(fieldId) ~> JsFunc("validationEngine", "showPrompt", S ? errorMessage, "error", "topRight", true)

  def hideValidationError(fieldId: String): JsCmd =
    JqId(fieldId) ~> JsFunc("validationEngine", "hide")

  def initFormValidation(formId: String): JsCmd = Jq("form") ~> JsFunc("validationEngine")

  def onErrorsCmd(errors: scala.Seq[FieldError]): Option[JsCmd] =
    errors.map {
      case FieldError(fieldId, errorMessage, onError) =>
        showValidationError(fieldId, errorMessage) & onError()
    }.reduceLeftOption(_ & _)

  def hideFormValidationError(errorMessage: String): JsCmd = JsCmds.Noop


  def showFormValidationError(errorMessage: String): JsCmd = Alert(errorMessage)
}


class CustomBootstrapFormValidation extends FormValidation {
  override def headAddition: NodeSeq = NodeSeq.Empty

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

  def initFormValidation(formId: String): JsCmd = JsCmds.Noop

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
