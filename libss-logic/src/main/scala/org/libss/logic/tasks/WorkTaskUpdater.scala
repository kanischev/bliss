package org.libss.logic.tasks

/**
 * date: 27.10.2014 17:44
 * author: Kaa
 */
trait WorkTaskUpdater {
  def taskStart(task:IWorkTask, message: String)
  def taskStateChange(task:IWorkTask, message: String)
  def taskExecutionError(task:IWorkTask, message: String)
  def taskFinishedWithError(task:IWorkTask, message: String)
  def taskSucceeded(task:IWorkTask, message: String)
}

