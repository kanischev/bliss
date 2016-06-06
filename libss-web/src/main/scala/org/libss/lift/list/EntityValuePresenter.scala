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
  def label: String

  /**
    * @return NodeSeq with rendered value
    */
  def renderValue: E => NodeSeq
}


