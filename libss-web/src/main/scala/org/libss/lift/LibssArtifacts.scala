package org.libss.lift

import net.liftweb.http.js.jquery.JQueryArtifacts
import net.liftweb.util.Props

/**
 * date: 09.04.14
 * author: Kaa
 */

trait LibssArtifactsHelper extends JQueryArtifacts {
  def jqueryVersion: String = "1.8.3"
  def twitterBootstrapFolder: String = "bootstrap3"

  override def pathRewriter: PartialFunction[List[String], List[String]] = {
    // JQuery
    case "js" :: "jquery.js" :: Nil if Props.devMode => List("js", "jquery", "jquery-" + jqueryVersion + ".js")
    case "js" :: "jquery.js" :: Nil => List("js", "jquery", "jquery-" + jqueryVersion + ".min.js")
    // Twitter Bootstrap
    case "css" :: "bootstrap.css" :: Nil if Props.devMode => List("css", twitterBootstrapFolder, "bootstrap.css")
    case "css" :: "bootstrap.css" :: Nil => List("css", twitterBootstrapFolder, "bootstrap.min.css")
    case "css" :: "bootstrap.css.map" :: Nil => List("css", twitterBootstrapFolder, "bootstrap.css.map")
    case "css" :: "bootstrap-theme.css" :: Nil if Props.devMode => List("css", twitterBootstrapFolder, "bootstrap-theme.css")
    case "css" :: "bootstrap-theme.css" :: Nil => List("css", twitterBootstrapFolder, "bootstrap-theme.min.css")
    case "css" :: "bootstrap-theme.css.map" :: Nil if Props.devMode => List("css", twitterBootstrapFolder, "bootstrap-theme.css.map")
    case "fonts" :: "glyphicons-halflings-regular.eot" :: Nil if Props.devMode => List("fonts", twitterBootstrapFolder, "glyphicons-halflings-regular.eot")
    case "fonts" :: "glyphicons-halflings-regular.svg" :: Nil if Props.devMode => List("fonts", twitterBootstrapFolder, "glyphicons-halflings-regular.svg")
    case "fonts" :: "glyphicons-halflings-regular.ttf" :: Nil if Props.devMode => List("fonts", twitterBootstrapFolder, "glyphicons-halflings-regular.ttf")
    case "fonts" :: "glyphicons-halflings-regular.woff" :: Nil if Props.devMode => List("fonts", twitterBootstrapFolder, "glyphicons-halflings-regular.woff")
  }
}

case object LibssArtifacts extends LibssArtifactsHelper {}