package org.libss.lift.util

import net.liftweb.http.S
import org.libss.util.helpers.OptionHelper

/**
  * Created by Kaa 
  * on 29.06.2016 at 01:43.
  */
trait RequestHelper extends OptionHelper {
  def withReplacedOrAddedParam(paramName: String, paramValues: List[String]) = {
    val request = S.request.toOption.safeGet("No request in scope")
    val paramsMap = request.params ++ Map(paramName -> paramValues)
    request.path.partPath.foldLeft("")((s, p) => s + "/" + p) + {
      if (paramsMap.isEmpty) "" //Should not happen
      else "?"+paramsMap.foldLeft(""){case (s, (k, vs)) => if (vs.isEmpty) (s + k + "=" + "&") else (s + vs.map(k + "=" + _).mkString("", "&", "&"))}.dropRight(1)
    }
  }

}
