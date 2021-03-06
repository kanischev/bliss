package org.libss.lift.form.fields

import net.liftweb.http.js.{JsCmd, JsExp}

/**
  * Created by Kaa 
  * on 08.06.2016 at 23:38.
  */
trait FieldValueAnalyzer[T] {
  def analyze(value: Option[T]): Option[FieldValueMessage]
  def clientSideAnalyze: Option[(JsExp) => JsCmd] = None
  def priority: Int
}

sealed trait FieldValueMessage {
  def messageText: String
  def preventsSubmit: Boolean
}

case class FieldValueErrorMessage(messageText: String) extends FieldValueMessage {
  def preventsSubmit = true
}

case class FieldValueWarningMessage(messageText: String) extends FieldValueMessage {
  def preventsSubmit = false
}

case class FieldValueSuccessMessage(messageText: String) extends FieldValueMessage {
  def preventsSubmit = false
}
