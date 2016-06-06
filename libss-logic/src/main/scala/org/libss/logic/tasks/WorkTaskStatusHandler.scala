package org.libss.logic.tasks

/**
 * date: 15.10.2014 17:21
 * author: Kaa
 */
/*
  Use {@handleWorkTaskStatusMessage} to tell hosting process
 */
trait WorkTaskStatusHandler {
  def handleWorkTaskStatusMessage(msg: IWorkTaskStatusMessage)
/*
  def workTaskStart(msg: T)
  def newWorkTaskStatus(msg: T)
  def workTaskFinish(msg: T)
  def workTaskError(workError: T)
*/
}
