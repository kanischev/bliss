package org.libss.logic.i18n

import java.text.{DecimalFormat, SimpleDateFormat}
import java.util.{Date, Locale, ResourceBundle}

/**
  * Created by Kaa 
  * on 04.06.2016 at 00:50.
  */
trait Formatter {
  implicit def locale: Locale
  lazy val formatterResources = ResourceBundle.getBundle("formatter", locale)

  val intFormat = "### ###0"
  val floatFormat = "### ###0.0###"

  val DateFormat        = new SimpleDateFormat(formatterResources.getString("date.format"), locale)
  val DateTimeFormat    = new SimpleDateFormat(formatterResources.getString("date-time.format"), locale)
  val SiteMapDateFormat = new SimpleDateFormat("yyyy-MM-dd", locale)

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


