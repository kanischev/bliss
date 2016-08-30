package org.libss.lift.form

import net.liftweb.http.S
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
  val ErrorMessage = S ? "validation.nonempty.error"

  def validate(value: Option[T]) = if (value.isDefined) None else {
    Some(ErrorMessage)
  }
}

case class NonEmptyStringValidator(errorMessage: String = S ? "validation.nonemptystring.error") extends NonEmptyValidator[String] {
  override def validate(value: Option[String]) = {
    val s = super.validate(value)
    if (s.isDefined) s
    else
    if (value.get.trim().isEmpty)
      Some(errorMessage)
    else
      None
  }
}

case class RegExpValidator[T](regex: String,
                              errorMessage: String,
                              caster: T => String = {(t:T) => t.toString}) extends FieldValidator[T] {
  def validate(value: Option[T]) = {
    value.flatMap(v => if (caster(v).matches(regex)) None else Some(errorMessage))
  }
}

case class EmailValidator(errorMessage: String = S ? "validation.email.error") extends FieldValidator[String] {
  val regexpValidator = RegExpValidator[String]("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", errorMessage)

  def validate(value: Option[String]) = regexpValidator.validate(value)
}

case class IntRangeValidator(fromInclusive: Int, tillInclusive: Int, errorMessage: String = S ? "validation.outofrange.error")
  extends FieldValidator[String]
    with SafeStringOptionHelper {

  def validate(value: Option[String]) = {
    val iv = value.flatMap(toIntOpt(_))
    if (iv.exists(v => v < fromInclusive || v > tillInclusive))
      Some(errorMessage.format(fromInclusive, tillInclusive))
    else
      None
  }
}

case class NumNotEqualsValidator(notAllowedNumericValues: Seq[Double], errorMessage: String = S ? "validation.notequal.error")
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


case class EqualFieldValuesValidator[T](field1: Field[_, T], field2: Field[_, T], errorMessage: (Option[T], Option[T]) => String = (a: Option[T], b: Option[T]) => S ? "validation.twofields.equality.error", requiredFields: Boolean = true) {
  private def validateField(f: Field[_, T], value: Option[T]) = {
    if (requiredFields) {
      if (f.value.isEmpty || f.value.exists(f1v => value.contains(f1v))) None
      else Some(errorMessage.apply(f.value, value))
    } else {
      if (f.value.exists(f1v => value.contains(f1v))) None
      else Some(errorMessage.apply(f.value, value))
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

case class StringLengthValidator(min: Option[Int], max: Option[Int], tooShortMessage: String = S ? "validation.valuelength.tooshort.error", tooLongMessage: String = S ? "validation.valuelength.toolong.error") extends FieldValidator[String] {
  def messageTooShort = tooShortMessage.format(min.get)
  def messageTooLong = tooLongMessage.format(max.get)

  override def validate(value: Option[String]) = {
    if (min.isDefined && value.exists(_.trim.length < min.get)) Some(messageTooShort)
    else if (max.isDefined && value.exists(_.trim.length > min.get)) Some(messageTooLong)
    else None
  }
}