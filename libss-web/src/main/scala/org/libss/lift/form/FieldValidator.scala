package org.libss.lift.form

import org.libss.util.helpers.{OptionHelper, SafeStringOptionHelper}

/**
  * Created by Kaa 
  * on 08.06.2016 at 23:40.
  */
trait FieldValidator[T] extends FieldValueAnalyzer[T] {
  def validate(value: Option[T]): Option[String]

  def analyze(value: Option[T]) = validate(value).map(FieldValueErrorMessage)

  def priority = 0
}

trait FieldWarnChecker[T] extends FieldValueAnalyzer[T] {
  def check(value: Option[T]): Option[String]

  def analyze(value: Option[T]) = check(value).map(FieldValueWarningMessage)

  def priority = 1
}

trait FieldSuccessChecker[T] extends FieldValueAnalyzer[T] {
  def check(value: Option[T]): Option[String]

  def analyze(value: Option[T]) = check(value).map(FieldValueSuccessMessage)

  def priority = 3
}

class NonEmptyValidator[T] extends FieldValidator[T] {
  val ErrorMessage = "Поле обязательно для заполнения"

  def validate(value: Option[T]) = if (value.isDefined) None else {
    Some(ErrorMessage)
  }
}

object NonEmptyStringValidator extends NonEmptyValidator[String] {
  val StringOfSpacesErrorMessage = "Поле должно содержать не только пробелы и символы конца строки"

  override def validate(value: Option[String]) = {
    val s = super.validate(value)
    if (s.isDefined) s
    else
    if (value.get.trim().isEmpty)
      Some(StringOfSpacesErrorMessage)
    else
      None
  }
}

case class RegExpValidator[T](regex: String,
                              error: String,
                              caster: T => String = {(t:T) => t.toString}) extends FieldValidator[T] {
  def validate(value: Option[T]) = {
    value.flatMap(v => if (caster(v).matches(regex)) None else Some(error))
  }
}

object EmailValidator extends FieldValidator[String] {
  val regexpValidator = RegExpValidator[String]("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", "Указан некорректный адрес электронной почты")

  def validate(value: Option[String]) = regexpValidator.validate(value)
}

case class IntRangeValidator(fromInclusive: Int, tillInclusive: Int)
  extends FieldValidator[String]
    with SafeStringOptionHelper {
  lazy val NotInRangeMsg = "Значение этого поля должно находиться в промежутке от %d до %d включительно".format(fromInclusive, tillInclusive)

  def validate(value: Option[String]) = {
    val iv = value.flatMap(toIntOpt(_))
    if (iv.exists(v => v < fromInclusive || v > tillInclusive))
      Some(NotInRangeMsg)
    else
      None
  }
}

case class NumNotEqualsValidator(notAllowedNumericValues: Seq[Double])
  extends FieldValidator[String]
    with OptionHelper
    with SafeStringOptionHelper {
  def valueNotAllowedMsg(notAllowedValue: String) = "Значение этого поля должно быть отлично от %s".format(notAllowedValue)

  def validate(value: Option[String]) = {
    val iv = value.flatMap(toDoubleOpt(_))
    if (iv.exists(v => notAllowedNumericValues.contains(v)))
      Some(valueNotAllowedMsg(value.safeGet("Should be defined here")))
    else
      None
  }
}


case class EqualFieldValuesValidator[T](field1: Field[_, T], field2: Field[_, T], requiredFields: Boolean = true) {
  def message = "Значения не совпадают"

  private def validateField(f: Field[_, T], value: Option[T]) = {
    if (requiredFields) {
      if (f.value.isEmpty || f.value.exists(f1v => value.contains(f1v))) None
      else Some(message)
    } else {
      if (f.value.exists(f1v => value.contains(f1v))) None
      else Some(message)
    }
  }

  def right = new FieldValidator[T] {
    def validate(value: Option[T]) = {
      validateField(field1, value)
    }
  }

  def left = new FieldValidator[T] {
    def validate(value: Option[T]) = {
      validateField(field2, value)
    }
  }
}

case class StringLengthValidator(min: Option[Int], max: Option[Int]) extends FieldValidator[String] {
  def messageToShort = "Значение поля должно быть длиной не менее %d символов!".format(min.get)
  def messageToLong = "Значение поля должно быть длиной не более %d символов!".format(max.get)

  override def validate(value: Option[String]) = {
    if (min.isDefined && value.exists(_.trim.length < min.get)) Some(messageToShort)
    else if (max.isDefined && value.exists(_.trim.length > min.get)) Some(messageToLong)
    else None
  }
}