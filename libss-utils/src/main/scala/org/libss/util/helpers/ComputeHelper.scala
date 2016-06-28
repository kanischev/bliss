package org.libss.util.helpers

/**
  * Created by Kaa 
  * on 29.06.2016 at 01:12.
  */
trait ComputeHelper {
  def divideWithOverflow(k: Long, m: Long) = {
    k / m + {
      if (k % m == 0) 0 else 1
    }
  }
}
