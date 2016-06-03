package org.libss.logic.references

import com.google.inject.{Inject, Provider}
import org.libss.util.helpers.OptionHelper
import scala.collection.JavaConversions._

/**
  * date: 02.06.2016 23:47
  * author: Kaa
  *
  * Basic references handling service implementation
  */
trait ReferenceGeneratorsHelper extends OptionHelper {
  def referenceGenerators: java.util.Set[ReferenceGenerator]

  def referenceGeneratorBy(referenceKey: ReferenceId) = {
    referenceGenerators
      .find(_.canHandleReference(referenceKey))
      .safeGet("No reference generator for reference type: " + referenceKey)
  }
}

trait ReferenceService {
  def getReferenceEntries(referenceKey: ReferenceId): Seq[ReferenceEntry]

  def getReferenceEntry(referenceKey: ReferenceId, referenceValueId: String): Option[ReferenceEntry]

  def getReferenceEntriesByLabel(referenceKey: ReferenceId, labelPart: String, limit: Int): Seq[ReferenceEntry]

  def getReferenceEntryByLabel(referenceKey: ReferenceId, label: String): Option[ReferenceEntry]
}

class ReferenceServiceImpl
  extends ReferenceService
  with ReferenceGeneratorsHelper {

  @Inject
  var referenceGeneratorsProvider: Provider[java.util.Set[ReferenceGenerator]] = _

  lazy val referenceGenerators = referenceGeneratorsProvider.get()

  def getReferenceEntries(referenceKey: ReferenceId) = referenceGeneratorBy(referenceKey).referenceEntriesWithEmptyBy(referenceKey)

  def getReferenceEntry(referenceKey: ReferenceId, referenceValueId: String) = {
    referenceGeneratorBy(referenceKey) match {
      case gen: SingleReferenceGenerator => gen.referenceEntryBy(referenceKey, referenceValueId)
      case g => g.referenceEntriesBy(referenceKey).find(_.uid == referenceValueId)
    }
  }

  def getReferenceEntriesByLabel(referenceKey: ReferenceId, labelPart: String, limit: Int) = {
    referenceGeneratorBy(referenceKey) match {
      case gen: LazyReferenceGenerator => gen.referenceEntriesBy(referenceKey, Option(labelPart), limit)
      case g => g.referenceEntriesBy(referenceKey).filter(_.value.contains(labelPart))
    }
  }

  def getReferenceEntryByLabel(referenceKey: ReferenceId, label: String) = {
    referenceGeneratorBy(referenceKey) match {
      case gen: SingleReferenceGenerator => gen.referenceEntryByLabel(referenceKey, label)
      case g => g.referenceEntriesBy(referenceKey).find(_.value == label)
    }
  }
}
