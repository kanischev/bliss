package org.libss.lift.util

import java.net.URLDecoder

import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.{AnonFunc, JsFunc, Str, _}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{JsCrVar, JsIf, Script, SetExp}
import net.liftweb.http.js.jquery.JqJE.Jq

/**
  * Created by Kaa 
  * on 08.06.2016 at 02:27.
  */
case class LocationHashManager(prefix: Option[String] = None) {
  val HashChangeListenVar = "listenForHashChange"
  val HashChangeEventName = "hashchange"

  def changeHash(hash: String) = {
    JsIf(JsNotEq(JsVar("window.location.hash"), "#" + hash),
      SetExp(JsVar(HashChangeListenVar), JsFalse) & SetExp(JsVar("window.location.hash"), "#" + hash))
  }

  def getHashAndCall(hashHandler: String => JsCmd): JsCmd = {
    SHtml.ajaxCall(JsRaw("window.location.hash"), hashHandler)._2.cmd
  }

  def initScript(onHashChange: String => JsCmd) =
    Script(
      JsCrVar(HashChangeListenVar, JsTrue) &
        (Jq(JsVar("window")) ~> JsFunc("bind",
          Str(HashChangeEventName),
          AnonFunc("e", JsIf(JsVar(HashChangeListenVar),
            getHashAndCall(onHashChange),              //if
            SetExp(JsVar(HashChangeListenVar), JsTrue) //else
          ))
        )).cmd
    )

}

object LocationHashManager {
  def parse(hashString: String) = {
    val unprefixed = if (!hashString.isEmpty && hashString(0) == '#') hashString.drop(1) else hashString
    if (unprefixed.isEmpty) Map.empty[String, String]
    else {
      val paramValueStrings = unprefixed.split('&')
      val pv = paramValueStrings.map(str => {
        val parts = str.split('=')
        parts(0) -> (if (parts.size > 1) URLDecoder.decode(parts(1), "utf8") else "")
      })
      pv.toMap
    }
  }

  def hashStringFrom(params: Map[String, String]) =
    params.map{case(k,v) => k + "=" + v}.mkString("&")
}