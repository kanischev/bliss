package org.libss.logic.tasks

import scala.collection.mutable
import org.slf4j.LoggerFactory
import com.google.inject.Inject

/**
 * date: 15.10.2014 17:26
 * author: Kaa
 */
trait WorkerQueue extends WorkTaskStatusHandler {
  /*
    Implement and inject via guice WorkTaskUpdater - to do more on task state change
   */
  @Inject(optional = true)
  protected var taskUpdater: WorkTaskUpdater = _

  protected var currentTask: Option[IWorkTask] = None
  protected val tasksQueue = mutable.Queue.empty[IWorkTask]
  protected val logger = LoggerFactory.getLogger(this.getClass)

  def initWorkerQueue(): Unit

  protected def handleUncaughtWorkTaskException(e: Throwable) = {
    logger.error("Uncaught exception during task run: " + currentTask.fold("")(_.toString), e)
  }

  protected def runNextTask() {
    if (!tasksQueue.isEmpty) {
      val nextTask = tasksQueue.dequeue()
      currentTask = Option(nextTask)
      try {
        nextTask.doWork(this)
      } catch {
        case e: Throwable => {
          handleUncaughtWorkTaskException(e)
        }
      } finally {
        if (!tasksQueue.isEmpty) runNextTask()
        else currentTask = None
      }
    } else {
      logger.error("RunNextTask on empty queue called")
    }
  }

  protected def addWorkTaskToQueue(newTask: IWorkTask) {
    this.tasksQueue.synchronized{
      tasksQueue.enqueue(newTask)
      if (tasksQueue.size == 1 && currentTask.isEmpty) runNextTask()
    }
  }
}
