package ru.softband.util.helpers

/**
  * date: 02.06.2016 23:02
  * author: Kaa
  *
  * Trait with safe string transformations to other class instances
  */
trait SafeStringOptionHelper {
  def inputStringToOptionClever(str: String) = {
    if (str == null || str.trim.isEmpty) None
    else Option(str)
  }

  protected def safeToOption[T](value: Option[String], converter: String => T): Option[T] = {
    try {
      value.map(converter)
    } catch {
      case _ => None
    }
  }

  implicit def toOptionImplicit[T](v: T) = new {
    def toOpt: Option[T] = Option(v)
  }

  implicit def toStringOption[T](v: Option[T]) = v.map(_.toString)

  implicit def toIntOpt(str: String): Option[Int] = safeToOption(str.toOpt, _.toInt)

  implicit def toLongOpt(str: String): Option[Long] = safeToOption(str.toOpt, _.toLong)

  implicit def toDoubleOpt(str: String): Option[Double] = safeToOption(str.toOpt, _.toDouble)

  implicit def toBigDecimalOpt(str: String): Option[BigDecimal] = safeToOption(str.toOpt, BigDecimal.apply(_))

  private def safeOptionToString[T](valueOption: Option[T], transform: T => String = (i: T) => i.toString): Option[String] = {
    try {
      valueOption.map(transform)
    } catch {
      case _ => None
    }
  }

  implicit def safeOptionDoubleToString(o: Option[Double]) = safeOptionToString(o)

  implicit def safeOptionLongToString(o: Option[Long]) = safeOptionToString(o)

  implicit def safeOptionIntToString(o: Option[Int]) = safeOptionToString(o)

  implicit def safeOptionBigDecimalToString(o: Option[BigDecimal]) = safeOptionToString(o)
}