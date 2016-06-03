package ru.softband.util.reflection

import scala.tools.scalap.scalax.rules.NoSuccess
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * date: 10.04.14
 * author: Kaa
 */
class PropertyChainParser extends JavaTokenParsers {
  val identString: Parser[String] = "[^\\()]+".r

  def mapField: Parser[MapFieldPathPart] = "(" ~> identString <~ ")" ^^ (mapKey => MapFieldPathPart(mapKey))

  def optionField: Parser[OptionFieldPathPart] = "[" ~> ident <~ "]" ^^ (optionField => OptionFieldPathPart(optionField))

  def simpleField: Parser[SimpleFieldPathPart] = ident ^^ (field => SimpleFieldPathPart(field))

  def fieldExp: Parser[FieldPathPart] = mapField | optionField | simpleField

  def pathParts: Parser[List[FieldPathPart]] = repsep(fieldExp, ".")

  def process(str: String) = {
    parseAll(pathParts, str) match {
      case Success(parsed, _) => parsed
      case e: NoSuccess => {
        println(e.msg + "   " + e.next)
        throw new IllegalArgumentException("Could not parse field path: " + str)
      }
    }
  }
}

object FieldPathParser {
  private val parser = new PropertyChainParser

  def parseFrom(str: String) = parser.process(str)
}
