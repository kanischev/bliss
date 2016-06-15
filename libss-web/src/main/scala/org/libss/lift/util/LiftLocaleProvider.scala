package org.libss.lift.util

import java.util.Locale

import net.liftweb.common.{Box, Empty}
import net.liftweb.http.{S, SessionVar}
import net.liftweb.util.Helpers
import org.libss.logic.i18n.LocaleProvider

/**
  * Created by Kaa 
  * on 12.06.2016 at 00:56.
  */

object LibssLocaleVar extends SessionVar[Box[Locale]](Empty) {
  override protected def __nameSalt = Helpers.nextFuncName
}

class LiftLocaleProvider extends LocaleProvider {
  override def getLocale: Locale = LibssLocaleVar.openOr(S.locale)
}
