package org.libss.lift.util

import net.liftweb.http.LiftRules

/**
  * Created by Kaa 
  * on 08.06.2016 at 02:23.
  */
trait UrlHelper {
  protected def prefixWithSplitterIfNeeded(path: String) =
    if (path.startsWith("/") || path.trim.isEmpty) path else "/" + path
}

trait ResourcePathHelper extends UrlHelper {
  def inClassPath(path: String) = {
    "/" + LiftRules.resourceServerPath + prefixWithSplitterIfNeeded(path)
  }
}