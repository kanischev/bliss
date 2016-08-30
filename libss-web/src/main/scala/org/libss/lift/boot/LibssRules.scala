package org.libss.lift.boot

import org.libss.lift.form.CustomBootstrapFormValidation
import org.libss.lift.list.{BootstrapTableEntityListRenderHelper, FixedLinksCount, PaginationRenderingStrategy}
import org.libss.lift.util.LiftLocaleProvider
import org.libss.logic.i18n.{LocaleProvider, Localizable}

/**
  * Created by Kaa 
  * on 21.06.2016 at 00:06.
  */
object LibssRules {
  var defaultTableTemplate = "libss" :: "itemsTableTemplate" :: Nil
  var defaultPageableTableTemplate = "libss" :: "pageableItemsTableTemplate" :: Nil
  var defaultLocaleProvider = new LiftLocaleProvider
  var defaultLanguage = "en"
  var defaultTableRendererHelper = BootstrapTableEntityListRenderHelper
  var defaultPaginationRenderingStrategy: PaginationRenderingStrategy = FixedLinksCount(7)
  var defaultFormValidator = new CustomBootstrapFormValidation
//  var defaultNoResultsRenderer =

}

trait LibssLocalizable extends Localizable {
  override def localeProvider: LocaleProvider = LibssRules.defaultLocaleProvider
}