package org.libss.util.reflection

import org.libss.util.helpers.ReflectionFieldValueHandler

import scala.collection.mutable

/**
  * Created by Kaa 
  * on 07.06.2016 at 22:35.
  */
trait FieldPathPart {
  def name: String

  def toPath: String

  def getFrom(obj: AnyRef): Option[Any]

  def setTo(obj: AnyRef, value: Option[Any])
}

case class SimpleFieldPathPart(name: String) extends FieldPathPart with ReflectionFieldValueHandler {
  def toPath = name

  def getFrom(obj: AnyRef) = getFieldValue[Any](name, obj)

  def setTo(obj: AnyRef, value: Option[Any]) {
    setFieldValue(obj, name, value.orNull)
  }
}


case class OptionFieldPathPart(name: String) extends FieldPathPart with ReflectionFieldValueHandler {
  def toPath = "[" + name + "]"

  def getFrom(obj: AnyRef) = getFieldValue[Option[Any]](name, obj).getOrElse(None)

  def setTo(obj: AnyRef, value: Option[Any]) {
    setFieldValue(obj, name, value)
  }
}

case class MapFieldPathPart(name: String) extends FieldPathPart {
  def toPath = "(" + name + ")"

  def getFrom(obj: AnyRef) = obj match {
    case m: Map[String, Any] => m.get(name)
    case m: mutable.Map[String, Any] => m.get(name)
  }

  def setTo(obj: AnyRef, value: Option[Any]) {
    obj match {
      case m: mutable.Map[String, Any] =>
        m.put(name, value.orNull)
      case m: Map[String, Any] =>
        throw new IllegalArgumentException("Can not set value %s to %s key of immutable Map".format("", ""))
    }
  }
}
