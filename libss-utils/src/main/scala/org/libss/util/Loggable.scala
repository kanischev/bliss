package org.libss.util

import org.slf4j.LoggerFactory

/**
  * Created by Kaa 
  * on 03.06.2016 at 05:20.
  */
trait Loggable {
  @transient lazy val Logger = LoggerFactory.getLogger(this.getClass)
}
