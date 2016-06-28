package org.libss.lift.boot

import org.libss.lift.list.{BootstrapTableEntityListRenderHelper, FixedLinksCount, PaginationRenderingStrategy}
import org.libss.lift.util.LiftLocaleProvider

/**
  * Created by Kaa 
  * on 21.06.2016 at 00:06.
  */
object LibssRules {
  var defaultTableTemplate = "libss" :: "itemsTableTemplate" :: Nil
  var defaultPageableTableTemplate = "libss" :: "pageableItemsTableTemplate" :: Nil
  var defaultLocaleProvider = new LiftLocaleProvider
  var defaultTableRendererHelper = BootstrapTableEntityListRenderHelper
  var defaultPaginationRenderingStrategy: PaginationRenderingStrategy = FixedLinksCount(7)

}
