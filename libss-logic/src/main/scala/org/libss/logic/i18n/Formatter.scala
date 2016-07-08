package org.libss.logic.i18n

import java.text.{DecimalFormat, SimpleDateFormat}
import java.util.{Date, Locale}

import com.google.inject.Inject
import org.libss.logic.guice.SafeInjection

/**
  * Created by Kaa 
  * on 04.06.2016 at 00:50.
  */
trait Formatter extends Localizable with SafeInjection {
  @Inject(optional = true)
  var localeProvider: LocaleProvider = _

  val bundleName = "i18n.Formatter"
  abstract override def resourceNames: List[String] = bundleName :: super.resourceNames

  val intFormat = i18n("format.number.int")
  val floatFormat = i18n("format.number.bigdecimal")

  val DateFormat        = new SimpleDateFormat(i18n("format.date"), Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT))
  val DateTimeFormat    = new SimpleDateFormat(i18n("format.date-time"), Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT))
  val SiteMapDateFormat = new SimpleDateFormat(i18n("format.date.sitemap"), Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT))

  def formatDate(date: Date) =
    Option(date).map(DateFormat.format).getOrElse("")

  def formatDateTime(date: Date) =
    Option(date).map(DateTimeFormat.format).getOrElse("")

  def formatBigDecimal(value: BigDecimal) = {
    if (value.equals(BigDecimal(value.longValue())))
      new DecimalFormat(intFormat).format(value)
    else
      new DecimalFormat(floatFormat).format(value)
  }
}


