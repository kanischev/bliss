package org.libss.lift.form

import org.libss.lift.list.EntityValuePresenter

import scala.collection.mutable.ListBuffer

/**
  * Created by Kaa 
  * on 07.06.2016 at 02:33.
  */
trait ValueObserver[T] {
  def onValueChange: (Option[T]) => Unit
}

/**
  * Base trait for entity field values handling
  * @tparam E - entity type
  * @tparam T - handled property type
  */
trait EntityValueHandler[E, T] {
  private val valueObservers = new ListBuffer[ValueObserver[T]]()

  /**
    * Adds value observer to be called on value change
    * @param observer
    */
  def addValueObserver(observer: ValueObserver[T]) = valueObservers.append(observer)

  /**
    * Removes value observer from list to be called on value change
    * @param observer
    * @return
    */
  def deleteValueObserver(observer: ValueObserver[T]) = {
    val idx = valueObservers.indexOf(observer)
    if (idx >= 0)
      valueObservers.remove(idx)
  }


  /**
    * field keeping current instance's field's value
    */
  private var _value: Option[T] = None

  /**
    * getter to obtain value being kept
    * @return
    */
  def value: Option[T] = _value

  /**
    * Setter to call to change keeping value. Observers are being told of value change
    * @return
    */
  def value_= (v: Option[T]): Unit ={
    _value = v
    valueObservers.foreach(vo => vo.onValueChange(v))
  }

  /**
    * @return Function for getting value of this field from entity
    */
  def getter: E => Option[T]

  /**
    * Sets value to E
    * for internal use
    */
  protected def setter: (E, Option[T]) => Unit

  /**
    * @param item sets value, keeped by this handler to provided entity
    */
  def callValueSetter(item: E) {
    setter.apply(item, value)
  }

  /**
    * Calls getter for this field on specified entity and sets result to holding _value field
    * @param obj - entity of class E to call getter on
    */
  def valueFrom(obj: E) {
    value = getter(obj)
  }
}

/**
  * @tparam E type of Entity to which this field would be bind
  * @tparam T type of value kept by the Field
  */
trait Field[E, T] extends EntityValueHandler[E, T] with EntityValuePresenter[E] {
  /**
    * @return '''true''' if this field is readonly
    */
  def readOnly: Boolean = false

  /**
    * Method should be overridden in case if you want special field value rendering behaviour
    *
    * @return NodeSeq for field value rendering
    */
//  def renderValue = (e) => getter(e).map(v => Formatter.formatWith(v, valueFormatter)).getOrElse(NodeSeq.Empty)
}