package org.libss.logic.client

/**
  * Created by Kaa 
  * on 04.06.2016 at 00:38
  * Base trait for Logging to users UI
  */
trait UserUILogger {
  /**
    * Logs message to users
    * @param message text to show
    * @param userToShow concrete user to show message to
    * @param keepUntilClosed don't dismiss message until user close
    * @param clearOthers if true specified - clears all previous messages
    */
  def log(message: String, userToShow: Option[String] = None, keepUntilClosed: Boolean = false, clearOthers: Boolean = false): Unit
  /**
    * Logs message as successful one to users
    * @param message text to show
    * @param userToShow concrete user to show message to
    * @param keepUntilClosed don't dismiss message until user close
    * @param clearOthers if true specified - clears all previous messages
    */
  def success(message: String, userToShow: Option[String] = None, keepUntilClosed: Boolean = false, clearOthers: Boolean = false): Unit
  /**
    * Logs message as errors to users
    * @param message text to show
    * @param userToShow concrete user to show message to
    * @param keepUntilClosed don't dismiss message until user close
    * @param clearOthers if true specified - clears all previous messages
    */
  def error(message: String, userToShow: Option[String] = None, keepUntilClosed: Boolean = false, clearOthers: Boolean = false): Unit
}