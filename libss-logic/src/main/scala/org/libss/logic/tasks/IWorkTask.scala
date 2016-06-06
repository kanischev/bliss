package org.libss.logic.tasks

import java.sql.Timestamp

import org.libss.logic.references.ReferenceId
import org.libss.util.helpers.ReflectionFieldValueHandler

import scala.xml.NodeSeq

/**
 * date: 15.10.2014
 * author: Kaa
 */
/**
  * Basic trait that describes work task parameter config
  */
trait   IWorkTaskParam {
  /**
    * Key for param to store in config
    * @return
    */
  def id: String

  /**
    * Label for input field in input forms
    * @return
    */
  def label: String

  /**
    * One of predefined references value that describes parameter value type
    * @return
    */
  def paramType: String

  /**
    * If field is of reference type here should be reference key
    * @return
    */
  def referenceId: Option[ReferenceId]

  /**
    * Whether it is disabled for user to change
    * @return
    */
  def disabled: Boolean

  /**
    * Whether this attribute's value is required for task config
    * @return
    */
  def required: Boolean

  /**
    * Field help contents
    * @return
    */
  def helpNS: Option[NodeSeq]
}

case class WorkTaskParam(
                         id: String,
                         label: String,
                         paramType: String,
                         referenceId: Option[ReferenceId] = None,
                         disabled: Boolean = false,
                         required: Boolean = false,
                         helpNS: Option[NodeSeq] = None
                         ) extends IWorkTaskParam

trait IWorkTaskParamValue {
  def paramId: String
  def referenceValue: String
  def dateTimeValue: Timestamp
  def colorValue: String
  def numericValue: Option[BigDecimal]
  def stringValue: String
  def booleanValue: Option[Boolean]

  def setReferenceValue(v: String)
  def setDateTimeValue(v: Timestamp)
  def setColorValue(v: String)
  def setNumericValue(v: Option[BigDecimal])
  def setStringValue(v: String)
  def setBooleanValue(v: Option[Boolean])
}

trait WorkTaskParamValueReflectionSetter extends IWorkTaskParamValue with ReflectionFieldValueHandler {
  override def setReferenceValue(v: String) = setFieldValue(this, "referenceValue", v)

  override def setDateTimeValue(v: Timestamp) = setFieldValue(this, "dateTimeValue", v)

  override def setColorValue(v: String) = setFieldValue(this, "colorValue", v)

  override def setNumericValue(v: Option[BigDecimal]) = setFieldValue(this, "numericValue", v)

  override def setStringValue(v: String) = setFieldValue(this, "stringValue", v)

  override def setBooleanValue(v: Option[Boolean]) = setFieldValue(this, "booleanValue", v)
}

case class WorkTaskParamValue(paramId: String,
                              referenceValue: String = null,
                              dateTimeValue: Timestamp = null,
                              colorValue: String = null,
                              numericValue: Option[BigDecimal] = None,
                              stringValue: String = null,
                              booleanValue:Option[Boolean] = None) extends WorkTaskParamValueReflectionSetter

trait IWorkTask {
  def workType: String
  def taskParamValues: Seq[IWorkTaskParamValue]
  def withNewParamValues(npv: Seq[IWorkTaskParamValue]): IWorkTask
  def taskParamsDescriptions: Seq[IWorkTaskParam]
  def doWork(workTaskStatusHandler: WorkTaskStatusHandler)
  override def toString = workType
  def onDeleteFromQueue(): Unit
}

trait InterruptableWorkTask extends IWorkTask {
  def interruptWorkTaskExecution(message: String): Unit
}

trait WorkTaskParamDescription{
  def label: String
  def valueType: String
}

trait ParametrizedWorkTask extends IWorkTask {
  def taskParametersDescription: Seq[WorkTaskParamDescription]
  def getParameters: Map[String, AnyRef]
}
