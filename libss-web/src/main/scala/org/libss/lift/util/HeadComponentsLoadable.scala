package org.libss.lift.util

import net.liftweb.http.js.JsCmd

/**
  * Created by Kaa 
  * on 30.08.2016 at 00:35.
  */
trait HeadComponentsLoadable {
  /**
    * @return the map of key of pack of libraries to list of library resources to be loaded
    */
  def headComponents: Map[String, List[String]]

  /**
    * @return JS to be run after all libraries components loading is done
    */
  def afterComponentsLoaded: Option[() => JsCmd] = None
}

object HeadComponents {
  def validationEngine(language: String) = Map("validation-engine" -> List(
    "/js/jquery/jquery.validationEngine.js",
    s"/js/jquery/jquery.validationEngine-$language.js",
    "/css/validation/validationEngine.jquery.css"
  ))
}