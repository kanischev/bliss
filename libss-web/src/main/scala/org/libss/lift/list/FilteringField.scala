package org.libss.lift.list

import net.liftweb.http.js.{JsCmd, JsCmds}
import org.libss.lift.form.fields.EntityValueHandler

/**
  * Created by Kaa 
  * on 29.06.2016 at 00:31.
  */
/**
  * Base trait for all filtering controls
  *
  * @tparam E Type of filtering objects
  * @tparam T Type of filtering object's field, being handled by this filtering control
  */
trait FilteringField[E, T] extends EntityValueHandler[E,  T] {
  def fieldName: String

  def setFrom(strValue: Option[String])

  def serialize: String = value.map(_.toString).getOrElse("")

  def syncFieldValueCmd: JsCmd

  def onRerenderCmd: JsCmd = JsCmds.Noop
}

