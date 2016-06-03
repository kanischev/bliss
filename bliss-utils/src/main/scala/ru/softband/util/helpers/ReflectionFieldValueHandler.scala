package ru.softband.util.helpers

import java.lang.reflect.Field

/**
  * date: 02.06.2016 22:44
  * author: Kaa
  *
  * Reflection field value getter helper trait
  */
trait ReflectionFieldValueHandler {
  protected def setFieldValue(instance: AnyRef, field: Field, value: Any) {
    field.setAccessible(true)
    field.set(instance, value)
  }

  protected def setFieldValue(instance: AnyRef, fieldName: String, value: Any) {
    setFieldValue(instance, instance.getClass.getDeclaredField(fieldName), value)
  }

  protected def getFieldValue[T <: Any](field: Field, instance: AnyRef): Option[Any] = {
    field.setAccessible(true)
    Option(field.get(instance))
  }

  protected def getFieldValue[T <: Any](fieldName: String, instance: AnyRef): Option[T] = {
    getFieldValue(instance.getClass.getDeclaredField(fieldName), instance).asInstanceOf[Option[T]]
  }
}
