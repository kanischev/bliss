package ru.softband.util.helpers

/**
  * date: 02.06.2016 22:44
  * author: Kaa
  *
  * Generic builder trait that helps builders usage
  */
trait GenericBuilder[T <: GenericBuilder[T]] {
  self: T =>

  private def noTransform(t: T): T = this

  def extend(transform: T => T): T = transform(this)

  def extendIf(condition: Boolean, transform: => T => T): T =
    extendIfElse(condition, transform, noTransform)

  def extendIfElse(condition: Boolean, transform: => T => T, elseTransform: => T => T): T =
    if (condition) extend(transform) else extend(elseTransform)

  def extendOpt(transform: Option[T => T]): T = transform map (_ apply this) getOrElse this
}
