package org.libss.lift.util

import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.{JsObj, _}
import net.liftweb.http.js.{JsCmd, JsExp}
import net.liftweb.http.js.JsCmds.JsIf
import net.liftweb.http.js.jquery.JqJE.Jq
import net.liftweb.json._

/**
  * Created by Kaa 
  * on 08.06.2016 at 02:26.
  */
case class TokenedValue[T](token: Long, value: T)

trait TokenedAjaxCall {
  implicit val formats = DefaultFormats
  def tokenParamName = "Token"

  def tokenedValueHandler[T](rawValueHandler: (Option[T]) => JsCmd, tokenPrefix: String = "")(implicit mf: Manifest[T]): (String) => JsCmd = (s) => {
    // TODO: Find out better way of doing the same things
    val tmp = parse(s) \ "value"
    val token = parse(s) \ "token"
    val tv = {
      try {
        Some(tmp.extract[T])
      } catch {
        case e: Throwable => None
      }
    }
    JsIf(JsEq(Jq(JsVar("document")) ~> JsFunc("data", tokenPrefix + tokenParamName), Num(token.asInstanceOf[JInt].values.longValue())), SHtml.ajaxInvoke(() => rawValueHandler(tv))._2.cmd)
  }

  def tokenedCallParam(paramExp: JsExp, tokenPrefix: String = "") = JsRaw("JSON.stringify(" + JsObj("token" -> Jq(JsVar("document")) ~> JsFunc("data", tokenPrefix + tokenParamName), "value" -> paramExp).toJsCmd + ")")
}
