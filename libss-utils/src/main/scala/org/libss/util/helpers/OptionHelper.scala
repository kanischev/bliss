package org.libss.util.helpers

/**
  * date: 02.06.2016 22:44
  * author: Kaa
  *
  * Option wrapper to implicitly shorten ensuring(_.isDefined, msg).get call to single method
  */
trait OptionHelper {

  final case class RichOption[T](opt: Option[T]) {
    def safeGet(errorMessage: String) = opt.ensuring(_.isDefined, errorMessage).get
  }

  implicit def richOption[T](option: Option[T]) = RichOption(option)
}

