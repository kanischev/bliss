package org.libss.logic.references

/**
  * date: 02.06.2016 23:47
  * author: Kaa
  *
  * Base traits and classes for logic References handling
  */
trait ReferenceGenerator {
  def referenceEntriesBy(referenceId: ReferenceId): Seq[ReferenceEntry]

  def referenceEntriesWithEmptyBy(referenceId: ReferenceId): Seq[ReferenceEntry] = {
    {if (referenceId.withEmptyValue)
      Seq(ReferenceEntry("",""))
    else
      Seq.empty[ReferenceEntry]} ++
      referenceEntriesBy(referenceId)
  }

  def canHandleReference(referenceId: ReferenceId): Boolean
}

trait SingleReferenceGenerator {
  self: ReferenceGenerator =>
  def referenceEntryBy(referenceId: ReferenceId, uid: String): Option[ReferenceEntry]

  def referenceEntryByLabel(referenceId: ReferenceId, label: String): Option[ReferenceEntry]
}

trait LazyReferenceGenerator {
  self: ReferenceGenerator =>
  def referenceEntriesBy(referenceId: ReferenceId, labelPart: Option[String], limit: Int = 10): Seq[ReferenceEntry]
}


case class ReferenceEntry(uid: String, value: String)
