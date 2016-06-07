package org.libss.lift.util

import net.liftweb.common.{Box, Empty}
import net.liftweb.http.{RequestVar, S}

/**
  * Created by Kaa 
  * on 08.06.2016 at 02:24.
  */
trait RequestInfoKeeper {
  private object requestParams extends RequestVar[Box[Map[String, List[String]]]](Empty)
  private object requestAttrs extends RequestVar[Box[Map[String, String]]](Empty)

  protected def keepParams() {
    requestParams.set(S.request.map(_.params))
  }

  protected def keepAttrs() {
    requestAttrs.set(Box(S.attrsFlattenToMap))
  }

  protected def keepAll() {
    keepAttrs()
    keepParams()
  }

  protected def getSingleRequestParam(paramName: String) = requestParams.get.flatMap(_.get(paramName).getOrElse(Nil).headOption)

  protected def getRequestParam(paramName: String) = requestParams.get.flatMap(_.get(paramName)).getOrElse(Nil)

  protected def getRequestAttr(attrName: String) = requestAttrs.get.flatMap(_.get(attrName))
}
