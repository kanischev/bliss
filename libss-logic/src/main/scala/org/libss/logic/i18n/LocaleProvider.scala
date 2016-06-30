package org.libss.logic.i18n

import java.util.Locale

/**
  * Created by Kaa 
  * on 09.06.2016 at 00:33.
  */
trait LocaleProvider {
  def getLocale: Locale

  def getDefaultLocale = Locale.ROOT
}

case class DefiniteLocaleProvider(locale: Locale) extends LocaleProvider {
  override def getLocale: Locale = locale
}