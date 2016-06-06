package org.libss.logic.tasks

/**
 * date: 15.10.2014 17:22
 * author: Kaa
 */
trait IWorkTaskStatusMessage {
  def message: String
}

case class StartWorkTaskExecution(message: String) extends IWorkTaskStatusMessage
case class NewWorkTaskStatusMessage(statusDescription: String) extends IWorkTaskStatusMessage {
  override def message = statusDescription
}
case class WorkTaskExecutionError(message: String) extends IWorkTaskStatusMessage
case class FinishWorkTaskExecution(message: String, error: Option[String]) extends IWorkTaskStatusMessage