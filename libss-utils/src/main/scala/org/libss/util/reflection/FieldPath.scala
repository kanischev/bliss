package org.libss.util.reflection

import java.sql.Timestamp
import java.util.Date

import org.libss.util.helpers.{Color, ReflectionFieldValueHandler, SafeStringOptionHelper}

import scala.reflect.ClassTag



/**
 * Class for handling path to field
 */
case class FieldPath[T](parts: Seq[FieldPathPart]) extends ReflectionFieldValueHandler {
  def toPath = parts.map(_.toPath).mkString(".")

  private def getFrom(instance: AnyRef, pathParts: Seq[FieldPathPart]): Option[Any] = {
    pathParts.foldLeft(Option(instance.asInstanceOf[Any]))((o, pathPart) => {
      o.flatMap(t => pathPart.getFrom(t.asInstanceOf[AnyRef]))
    })
  }

  def getFrom(instance: AnyRef): Option[T] = {
    val v = getFrom(instance, parts)
    v.map(_.asInstanceOf[T])
  }

  def setTo(instance: AnyRef, valueToSet: Option[T]) {
    getFrom(instance, parts.init).foreach(v => parts.last.setTo(v.asInstanceOf[AnyRef], valueToSet))
  }
}

object FieldPath /*extends PathByFieldFun */{
  def apply[T]() = new FieldPath[T](Seq.empty[FieldPathPart])

  def apply[T](pathToParse: String) = new FieldPath[T](FieldPathParser.parseFrom(pathToParse))

//  def apply[T, F](fun: (T) => Option[F])(implicit ct: ClassTag[T]) = new FieldPath[F](pathByFieldFun(ct.runtimeClass.asInstanceOf[Class[T]], fun))
}

trait FieldPathHelpers {
  implicit def extendFieldPath(fp: FieldPath[_]) = ExtendedFieldPath(fp)

  case class ExtendedFieldPath(fp: FieldPath[_]) {
    def toFieldId = fp.parts.map(_.name).mkString("-")
  }
}

/**
 * @tparam A is type of value holded by field
 */
trait FieldPathConversionHelper[A] extends ReflectionFieldValueHandler  with FieldPathHelpers {

  def holderClass(implicit ct: ClassTag[A]) = ct.runtimeClass

  def converterTo[T](value: Option[A])(implicit ct: ClassTag[T]): Option[T] = value.map(_.asInstanceOf[T]).flatMap(Option(_))

  def converterFrom[T](value: Option[T])(implicit ct: ClassTag[T]): Option[A] = value.map(_.asInstanceOf[A]).flatMap(Option(_))

  /*

    implicit def toFieldPath(s: String) = FieldPath[A](s)

    def fieldPathBy[E, S](fun: (E) => Option[S])(implicit ct: ClassTag[E]) = FieldPath[E, S](fun)
  */

  protected def getterBy[B, T](fp: FieldPath[T])(implicit ct: ClassTag[T]): (B) => Option[A] = (b) => converterFrom[T](fp.getFrom(b.asInstanceOf[AnyRef]))

  protected def setterBy[B, T](fp: FieldPath[T])(implicit ct: ClassTag[T]): (B, Option[A]) => Unit = (inst, v) => fp.setTo(inst.asInstanceOf[AnyRef], converterTo[T](v))
}

trait StringFieldPathConversionHelper extends FieldPathConversionHelper[String] with SafeStringOptionHelper {
  private def toBooleanOpt(value: String) = value match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }

  override def converterTo[T](value: Option[String])(implicit ct: ClassTag[T]) = {
    val t = value.flatMap(v => {
      ct.toString match {
        case "java.lang.String" => if (v == null || v.trim.isEmpty) None else value
        case "Int" => toIntOpt(v)
        case "Long" => toLongOpt(v)
        case "Double" => toDoubleOpt(v)
        case "BigDecimal" => toBigDecimalOpt(v)
        case "scala.math.BigDecimal" => toBigDecimalOpt(v)
        case "Boolean" => toBooleanOpt(v)
        case _ => None
      }
    }).map(_.asInstanceOf[T])
    t
  }

  override def converterFrom[T](value: Option[T])(implicit ct: ClassTag[T]) = {
    value.flatMap(_ match {
      case c: Int => safeOptionIntToString(Option(c))
      case c: Long => safeOptionLongToString(Option(c))
      case c: Double => safeOptionDoubleToString(Option(c))
      case bd: java.math.BigDecimal => safeOptionBigDecimalToString(Option(bd))
      case c: BigDecimal => safeOptionBigDecimalToString(Option(c))
      case b: Boolean => Some(if (b) "true" else "false")
      case _ => value.map(_.toString).flatMap(strV => if (strV == null || strV.trim.isEmpty) None else Option(strV))
    })
  }
}

trait SeqStringFieldPathConversionHelper extends FieldPathConversionHelper[Seq[String]] with SafeStringOptionHelper {

  override def converterTo[T](value: Option[Seq[String]])(implicit ct: ClassTag[T]) = super.converterTo(value)(ct)

  override def converterFrom[T](value: Option[T])(implicit ct: ClassTag[T]) = super.converterFrom(value)(ct)

}

// TODO: Awful copy-paste here!!!
trait SeqLongFieldPathConversionHelper extends FieldPathConversionHelper[Seq[Long]] with SafeStringOptionHelper {

  override def converterTo[T](value: Option[Seq[Long]])(implicit ct: ClassTag[T]) = super.converterTo(value)(ct)

  override def converterFrom[T](value: Option[T])(implicit ct: ClassTag[T]) = super.converterFrom(value)(ct)

}

trait DateFieldPathConversionHelper extends FieldPathConversionHelper[Date] {
  override def converterTo[T](value: Option[Date])(implicit ct: ClassTag[T]) = {
    val t = value.flatMap(v => {
      ct.toString match {
        case "java.sql.Date" => Some(new java.sql.Date(v.getTime))
        case "java.util.Date" => Some(v)
        case "java.sql.Timestamp" => Some(new Timestamp(v.getTime))
        case _ => None
      }
    }).map(_.asInstanceOf[T])
    t
  }

  override def converterFrom[T](value: Option[T])(implicit ct: ClassTag[T]) = {
    value.flatMap(_ match {
      case c if c.isInstanceOf[Date] => Some(new java.util.Date(c.asInstanceOf[Date].getTime))
    })
  }
}



trait ColorFieldPathConversionHelper extends FieldPathConversionHelper[Color] {
  override def converterTo[T](value: Option[Color])(implicit ct: ClassTag[T]) = {
    val t = value.flatMap(v => {
      ct.toString match {
        case "java.lang.String" => Some(v.toHexString)
        case "Int" => Some(v.toSingleInt)
        case "Long" => Some(v.toSingleInt.toLong)
        case _ => None
      }
    }).map(_.asInstanceOf[T])
    t
  }

  override def converterFrom[T](value: Option[T])(implicit ct: ClassTag[T]) = {
    value.flatMap(v => try{v match {
      case c: Int => Some(Color(c))
      case s: String => Some(Color(s))
      case _ => None
    }} catch {
      case e: Exception => None
    })
  }
}