package org.libss.logic.tasks

import java.util.Date

/**
 * date: 16.10.2014 14:06
 * author: Kaa
 */
trait IWorkTaskScheduler {
  def scheduledTasksBefore(dateTime: Date): Seq[(Date, IWorkTask)]

  /*
    Should finish previously ran tasks (with setting errors or smth)
    Should restore queued tasks from DB (were queued during previous system run but were not ran)
   */
  def initScheduler(): Unit

  def scheduleTask(date: Date, task: IWorkTask, reschedulePeriod: Option[Long] = None): String
  def scheduleTaskIfNotScheduled(date: Date, task: IWorkTask, reschedulePeriod: Option[Long] = None): Option[String]

  def addTaskToWorkerQueue(task: IWorkTask): Unit
  def addTasksToWorkerQueue(tasks: Seq[IWorkTask]): Unit
  def reschedule(): Unit

  def runOverdueTasks(time: Option[Date] = None) {
    val currentTime = time.getOrElse(new Date())
    val tasksToRun = scheduledTasksBefore(currentTime)
    if (tasksToRun.nonEmpty) {
      addTasksToWorkerQueue(tasksToRun.sortBy(_._1).map(_._2))
    }
  }
}

case object WorkTaskSchedulerMessage
