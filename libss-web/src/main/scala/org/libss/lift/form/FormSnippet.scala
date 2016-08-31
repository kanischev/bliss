package org.libss.lift.form

import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers
import org.libss.lift.form.fields.FormField

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 31.08.2016 at 03:31.
  */
trait FormSnippet[E] {
  var formId = Helpers.nextFuncName

  def template: NodeSeq
  def fields(item: Option[E]): Seq[FormField[E, _]]

  def formValidators(item: Option[E]): Seq[FormValidator[E]]
  def renderForm(item: E)
}

trait FormValidator[T] {
  def validate(fields: Seq[FormField[T, _]]): Option[JsCmd]
}

trait NonAjaxFormSnippet[E] extends FormSnippet[E]

trait AjaxFormSniipet[E] extends FormSnippet[E]
