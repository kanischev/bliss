package org.libss.lift.util

import net.liftweb.util.PCDataXmlParser
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.libss.logic.templating.StringToXmlParser

import scala.xml.{Elem, XML}
import scala.xml.factory.XMLLoader

/**
  * Created by Kaa 
  * on 12.06.2016 at 01:59.
  */
class StringToXmlParserImpl extends StringToXmlParser {
  def parseXMLString(str: String) = PCDataXmlParser(str)

  def parseHTMLString(str: String) = {
    try{
      Some(TagSoupXmlLoader.get().loadString(str))
    } catch {
      case e: Exception => None
    }
  }

  def parseHTMLFragmentString(str: String) = {
    try{
      val parsedBody = TagSoupXmlLoader.get().loadString(str) \\ "html" \\ "body"
      Some(parsedBody(0).child)
    } catch {
      case e: Exception => None
    }
  }
}

object TagSoupXmlLoader {

  private val factory = new SAXFactoryImpl()

  def get(): XMLLoader[Elem] = {
    XML.withSAXParser(factory.newSAXParser())
  }
}