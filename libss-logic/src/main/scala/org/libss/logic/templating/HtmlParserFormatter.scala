package org.libss.logic.templating

import com.google.inject.Inject
import org.libss.logic.guice.Injection

/**
  * Created by Kaa 
  * on 07.06.2016 at 01:54.
  */

class HtmlParserFormatter extends Injection {
  @Inject
  protected var parser: StringToXmlParser = _

  def formatString(str: String) = parser.parseHTMLFragmentString(str)
}