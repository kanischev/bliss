package org.libss.logic.templating

import com.google.inject.Inject
import org.fusesource.scalate.{TemplateEngine, TemplateSource}
import org.libss.logic.guice.Injection

import scala.xml.{Elem, NodeSeq}

/**
 * date: 27.07.12
 * author: Kaa
 */

trait ScalateTemplateRenderer[T] extends TypedTemplateRenderer[T] with Injection {
  @Inject
  protected var engine: TemplateEngine = _

  def convert(str: String): T

  def renderTemplate(templateId: String, params: Map[String, Any]) = convert(engine.layout(templateId, params))

  def renderTemplate(templateId: String, templateText: String, params: Map[String, Any]) = convert(
    engine.layout(templateId, engine.load(TemplateSource.fromText(templateId, templateText)), params)
  )
}

class ScalateRawTemplateRenderer extends ScalateTemplateRenderer[String] {
  def convert(str: String) = str
}

trait StringToXmlParser {
  def parseXMLString(str: String): Option[NodeSeq]
  def parseHTMLString(str: String): Option[Elem]
  def parseHTMLFragmentString(str: String): Option[NodeSeq]
}

class ScalateHtmlTemplateRenderer extends ScalateTemplateRenderer[NodeSeq] with Injection {
  @Inject
  protected var stringXmlParser: StringToXmlParser = _

  def convert(str: String) = stringXmlParser.parseHTMLString(str).getOrElse(NodeSeq.Empty)
}

class ScalateHtmlPartTemplateRenderer extends ScalateTemplateRenderer[NodeSeq] with Injection {
  @Inject
  protected var stringXmlParser: StringToXmlParser = _

  def convert(str: String) = stringXmlParser.parseHTMLFragmentString(str).getOrElse(NodeSeq.Empty)
}