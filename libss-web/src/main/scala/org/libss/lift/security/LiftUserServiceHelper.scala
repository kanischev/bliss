package org.libss.lift.security

import net.liftweb.common.{Box, Empty}
import net.liftweb.http.SessionVar
import net.liftweb.util.Helpers
import org.libss.logic.security.{User, UserService}
import org.libss.util.helpers.OptionHelper

/**
  * Created by Kaa 
  * on 12.06.2016 at 20:51.
  */
object LoginRedirect extends SessionVar[Box[String]](Empty) {
  override protected def __nameSalt = Helpers.nextFuncName
}

trait LiftUserServiceHelper[T <: User]
  extends UserService[T]
    with OptionHelper
    with LogInRedirectHelper {

}