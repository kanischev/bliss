package org.libss.lift.util

import net.liftweb.http.js.{JsObj, JsCmd, JsExp, HtmlFixer}
import net.liftweb.http.js.JE._
import net.liftweb.http.js.jquery.JqJE
import net.liftweb.http.js.JE.Call
import net.liftweb.http.js.JE.JsFunc
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.jquery.JqJE.Jq

/**
  * Created by Kaa
  * on 08.06.2016 at 02:34.
  */
trait JsExtensions extends HtmlFixer {
  case class JsNew(call: Call) extends JsExp {
    def toJsCmd = "new " + call.toJsCmd
  }

  trait JsExpHelper {
    self: JsExp =>

    def call(method: String, params: JsExp*) = this ~> JsFunc(method, params: _*)

    def getValue = call("val")

    def setValue(value: JsExp) = call("val", value)

    def getAttributeValue(attributeName: String) = call("attr", Str(attributeName))

    def setAttributeValue(attributeName: String, value: String) = call("attr", Str(attributeName), Str(value))

    def removeClass(className: String) = call("removeClass", Str(className))

    def addClass(className: String) = call("addClass", Str(className))

    def bind(eventType: String, handler: JsExp) = call("bind", Str(eventType), handler)

    def onBlur(params: JsExp*) = call("blur", params: _*)

    def blur = onBlur()
  }

  case class JqId(uid: String) extends JsExp with JsExpHelper {
    def toJsCmd = JqJE.JqId(Str(uid)).toJsCmd
  }

  object JsThis extends JsVar("this") with JsExpHelper

  case class ToNextFormElementCmd(elem: JsExp) extends JsCmd {
    def toJsCmd = """$(":input:not(:disabled):eq(" + ($(":input:not(:disabled)").index(""" + elem.toJsCmd + """) + 1) + ")").focus();"""
  }
}

case class JsWindowOpen(location: String, windowTitle: String, params: Map[String, String]) extends JsCmd {
  protected def stringLiteral(str: String) = "\'" + str + "\'"

  def toJsCmd = "window.open(" + Seq(location, windowTitle, params.toSeq.map{case(k, v) => k + "=" + v}.mkString(",")).map(stringLiteral(_)).mkString(", ") + ")"
}

case class JsOpenInTab(location: String) extends JsCmd {
  def toJsCmd = "window.open(\'" + location + "\', \'_blank\')"
}

case class HideModalOnOverlayClick(message: String = "") extends JsCmd {
  def toJsCmd = (Jq(Str(".blockOverlay")) ~> JsFunc("attr", Str("title"), Str(message)) ~> JsFunc("click", JsRaw("JQuery.unblockUI"))).toJsCmd
}

case class JsConsoleLog(msg: JsExp) extends JsCmd {
  def toJsCmd = (JsVar("console") ~> JsFunc("log", msg)).toJsCmd
}

object JsConsoleLog {
  def apply(str: String): JsConsoleLog = apply(Str(str))
}

case class JqSetTimeout(action: JsCmd, timeout: Int) extends JsCmd {
  def toJsCmd = JsFunc("setTimeout", AnonFunc("", action), Num(timeout)).toJsCmd
}

object ReloadCmd extends JsCmd {
  def toJsCmd = (JsVar("location") ~> JsFunc("reload")).toJsCmd
}

case class OnPageModalDialog(elementId: String, css: Option[JsObj] = None) extends JsCmd {
  val toJsCmd = "jQuery.blockUI({ message: $('#" + elementId + "')"  +
    css.map(",  css: " + _.toJsCmd + " ").getOrElse("") + "});"
}

/*
Promise.all(
        ['module1', 'module2', 'module3']
        .map(x => System.import(x)))
    .then(([module1, module2, module3]) => {
        // Use module1, module2, module3
    }); */
trait SystemJSHelper extends JsExtensions {
  def loadAndCall(modules: Seq[String], cmd: JsCmd): JsCmd = {
    if (modules.isEmpty) cmd
    else {
      (JsVar("Promise") ~> JsFunc("all", JsArray(modules.map(Str(_)): _*) ~>
        JsFunc("map", JsRaw("x => System.import(x)"))) ~>
          JsFunc("then", AnonFunc(cmd))).cmd
    }
  }

  def loadAndCall(modulesMap: Map[String, Seq[String]], cmd: JsCmd): JsCmd = {
    loadAndCall(modulesMap.values.flatten.toList.distinct, cmd)
  }

}
