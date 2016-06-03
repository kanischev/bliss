package org.libss.logic.references

/**
  * date: 02.06.2016 23:54
  * author: Kaa
  */
trait ReferenceId {
  def id: String
  def orderByLabel: Boolean
  def withEmptyValue: Boolean = true
}

case class ReferenceValueId(id: String)