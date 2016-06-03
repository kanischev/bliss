package ru.softband.util

import org.slf4j.LoggerFactory

/**
  * Created by Kaa 
  * on 03.06.2016 at 05:20.
  */
trait Loggable {
  lazy val Logger = LoggerFactory.getLogger(this.getClass)
}
