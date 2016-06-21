package org.libss.lift.list

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 07.06.2016 at 02:30.
  */
trait EntityValuePresenter[E] {
  /**
    * @return Human readable label for this field when it's being displayed
    */
  def renderLabel: NodeSeq

  /**
    * @return NodeSeq with rendered value
    */
  def renderValue: E => NodeSeq
}

object ColumnAlign extends Enumeration {
  type ColumnAlign = Value
  val LeftTop = Value("left-aligned top-aligned")
  val LeftMiddle = Value("left-aligned middle-aligned")
  val LeftBottom = Value("left-aligned bottom-aligned")
  val CenterTop = Value("center-aligned top-aligned")
  val CenterMiddle = Value("center-aligned middle-aligned")
  val CenterBottom = Value("center-aligned bottom-aligned")
  val RightTop = Value("right-aligned top-aligned")
  val RightMiddle = Value("right-aligned middle-aligned")
  val RightBottom = Value("right-aligned bottom-aligned")
}

trait TableColumn[E] extends EntityValuePresenter[E] {
  def clickable: Boolean

  def sortable: Boolean

  def valueAlignment: ColumnAlign.ColumnAlign
}

