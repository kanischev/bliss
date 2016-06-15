package org.libss.lift.util

import net.liftweb.http.LiftRules
import org.libss.util.helpers.UrlHelper

/**
  * Created by Kaa 
  * on 08.06.2016 at 02:23.
  */
trait ResourcePathHelper extends UrlHelper {
  def inClassPath(path: String) = {
    "/" + LiftRules.resourceServerPath + prefixWithSplitterIfNeeded(path)
  }
}