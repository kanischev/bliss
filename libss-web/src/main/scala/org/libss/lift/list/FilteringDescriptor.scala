package org.libss.lift.list

import org.libss.util.reflection.{FieldPath, FieldPathHelpers}

/**
  * Created by Kaa 
  * on 29.06.2016 at 00:28.
  */
/**
  * Base trait for Filtering descriptors: filtering form fields generating and values handling
  *
  * @tparam E Type of filtering object, corresponding to implementing descriptor
  */
trait FilteringDescriptor[E] extends FieldPathHelpers {
  def prefix: Option[String] = None

  implicit def filteringControlIdGenFun = (fPath: FieldPath[_]) => prefix.getOrElse("") + fPath.toFieldId

  def filteringFields: Seq[FilteringField[E, _]]

  def updateFilteringObjectWith(entity: E, fields: Seq[FilteringField[E, _]])

  def toStringMap: (Seq[FilteringField[E, _]]) => Map[String, String]

  def updateFromMap(filterControls: Seq[FilteringField[E, _]], params: Map[String, String])

  def updateControlsWith(filterControls: Seq[FilteringField[E, _]], entity: E)
}
