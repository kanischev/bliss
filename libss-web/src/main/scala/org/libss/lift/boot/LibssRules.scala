package org.libss.lift.boot

import org.libss.lift.list.{BootstrapTableEntityListRenderHelper, FixedLinksCount}

/**
  * Created by Kaa 
  * on 21.06.2016 at 00:06.
  */
object LibssRules {
  var defaultTableTemplate = "libss" :: "itemsListTemplate" :: Nil
  var defaultTableRendererHelper = BootstrapTableEntityListRenderHelper
  var defaultPaginationRenderingStrategy = FixedLinksCount(7)

}
