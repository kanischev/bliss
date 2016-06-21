package org.libss.util

/**
  * Created by Kaa 
  * on 22.06.2016 at 01:41.
  */
trait PageableBase {
  var page: Long = 1L
  var itemsPerPage: Long = 10L
}

object SortOrdering {
  val Asc = "asc"
  val Desc = "desc"
}

trait SortableBase {
  var sort: String = null
  var ordering: String = null
}
