package org.libss.logic.i18n

import java.text.{DecimalFormat, SimpleDateFormat}
import java.util.{Date, Locale}

import com.google.inject.Inject
import org.libss.logic.guice.Injection

/**
  * Created by Kaa 
  * on 04.06.2016 at 00:50.
  */
trait Formatter extends Injection {
  @Inject(optional = true)
  protected var localeProvider: LocaleProvider = _

  val bundleName = "i18n.Formatter"

  lazy val formatterResource = DefiniteLocalizedResource(bundleName)

  val intFormat = formatterResource.getProp("format.number.int")
  val floatFormat = formatterResource.getProp("format.number.bigdecimal")

  val DateFormat        = new SimpleDateFormat(formatterResource.getProp("format.date"), Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT))
  val DateTimeFormat    = new SimpleDateFormat(formatterResource.getProp("format.date-time"), Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT))
  val SiteMapDateFormat = new SimpleDateFormat(formatterResource.getProp("format.date.sitemap"), Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT))

  def formatDate(date: Date) =
    Option(date).map(DateFormat.format).getOrElse("")

  def formatDateTime(date: Date) =
    Option(date).map(DateTimeFormat.format).getOrElse("")

  def formatBigDecimal(value: BigDecimal) = {
    if (value.equals(BigDecimal(value.intValue())))
      new DecimalFormat(intFormat).format(value)
    else
      new DecimalFormat(floatFormat).format(value)
  }
}


