package org.libss.lift.list

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 22.06.2016 at 01:48.
  */
trait FilteredTableItemsListSnippet[E, F] {

  /**
    * Override if you need few such snippets on one page
    *
    * @return
    */
  def prefix: Option[String] = None

  /**
    * Should be implemented by API user
    *
    * @return Quantity of items satisfying the filter quantity
    */
  def countAllItems(filter: F): Long

  /**
    * Should be implemented by API user
    *
    * @param filter - filtering object
    * @return should return the list of items satisfying the filter
    */
  def rowsBy(filter: F): Seq[E]

  /**
    * Descriptor for handling filtering form
    *
    * @return typed with filtering object type FilteringDescriptor instance
    */
  def filteringDescriptor:  FilteringDescriptor[F]

  /**
    * Default filtering object creation function
    *
    * @return filtering object with default field values
    */
  def defaultFilteringObject: F

  /**
    * Actually render method
    *
    * @return Rendered NodeSeq
    */
  def render: NodeSeq

  protected def renderNoResults(columnDescriptions: Seq[EntityValuePresenter[E]]): NodeSeq
}

//TODO
trait FilteringDescriptor[T]

