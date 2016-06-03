package org.libss.logic.squeryl

import org.libss.util.helpers.ReflectionFieldValueHandler
import java.util.UUID

/**
 * date: 03.06.2016 00:10
 * author: Kaa
 */

trait IdGeneratorEntity[A, T] {
  def id: T
  def withId(newId: T): A
  def generateId: T
  def isPersisted: Boolean
  def mkId() = withId(generateId)
}

trait SquerylEntityPersistedHelper extends ReflectionFieldValueHandler {
  def markAsPersisted() {
    setFieldValue(this, "_isPersisted", Boolean.box(true))
  }
}

trait UuidKeyedEntity[A] extends IdGeneratorEntity[A, String] {
  def generateId = UUID.randomUUID().toString
  def isPersisted: Boolean = id != null
}

trait IdPresetUuidKeyedEntity[A] extends UuidKeyedEntity[A] {
  def persistedFlag: Boolean
  override def isPersisted = id != null && persistedFlag
}

trait LongKeyedEntity {
  def id: Long
}

trait LongKeyedEntityWithPersistedFlag {
  def id: Long
  def persistedFlag: Boolean
}