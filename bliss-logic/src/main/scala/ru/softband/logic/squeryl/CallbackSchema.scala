package ru.softband.util.squeryl

import org.squeryl.{KeyedEntityDef, Schema}
import org.squeryl.PrimitiveTypeMode._

/**
  * date: 03.06.2016 00:05
  * author: Kaa
  *
  * Basic Squeryl Schema class with some improvementa
  */

class CallbackSchema extends Schema {
  implicit def kedForUUIDKeyedEntity[T <: UuidKeyedEntity[T]] = new UuidKED[T]{}
  implicit def kedForLongKeyedEntity[T <: LongKeyedEntity] = new LongKeyedKED[T]{}
  implicit def kedForLongKeyedEntityWithPersistedFlag[T <: LongKeyedEntityWithPersistedFlag] = new LongKeyedWithPersistedFlagKED[T]{}

  trait UuidKED[A <: UuidKeyedEntity[A]] extends KeyedEntityDef[A, String] {
    def getId(a: A) = a.id
    def isPersisted(a: A) = a.id != null && a.isPersisted
    def idPropertyName = "id"
  }

  trait LongKeyedKED[A <: LongKeyedEntity] extends  KeyedEntityDef[A, Long] {
    def getId(a: A) = a.id
    def isPersisted(a: A) = a.id > 0
    def idPropertyName = "id"
  }

  trait LongKeyedWithPersistedFlagKED[A <: LongKeyedEntityWithPersistedFlag] extends  KeyedEntityDef[A, Long] {
    def getId(a: A) = a.id
    def isPersisted(a: A) = a.id > 0 && a.persistedFlag
    def idPropertyName = "id"
  }

  override def callbacks = Seq(
    beforeInsert[IdGeneratorEntity[_, _]]() map (a => {
      if (!a.isPersisted && a.id == null)
        a.mkId().asInstanceOf[IdGeneratorEntity[_, _]]
      else
        a
    })
  )
}

object CallbackSchema extends CallbackSchema