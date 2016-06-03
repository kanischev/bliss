package org.libss.util.helpers

import org.libss.util.Loggable
import org.slf4j.LoggerFactory

import scala.util.parsing.combinator.JavaTokenParsers

/**
  * date: 02.06.2016 23:00
  * author: Kaa
  *
  * Transliteration helper trait for Ru -> Translit text handling
  */
trait Transliterator {
  def transliterate(input: String): String
}

trait TransliterationHelper extends Transliterator {
  val dictionary = Map(
    "А" -> "A",
    "Б" -> "B",
    "В" -> "V",
    "Г" -> "G",
    "Д" -> "D",
    "Е" -> "E",
    "Ё" -> "Yo",
    "Ж" -> "Zh",
    "З" -> "Z",
    "И" -> "I",
    "Й" -> "Y",
    "К" -> "K",
    "Л" -> "L",
    "М" -> "M",
    "Н" -> "N",
    "О" -> "O",
    "П" -> "P",
    "Р" -> "R",
    "С" -> "S",
    "Т" -> "T",
    "У" -> "U",
    "Ф" -> "F",
    "Х" -> "H",
    "Ц" -> "Tz",
    "Ч" -> "Ch",
    "Ш" -> "Sh",
    "Щ" -> "Sch",
    "Ъ" -> "\'",
    "Ы" -> "Y",
    "Ь" -> "\'",
    "Э" -> "E",
    "Ю" -> "Yu",
    "Я" -> "Ya",
    "а" -> "a",
    "б" -> "b",
    "в" -> "v",
    "г" -> "g",
    "д" -> "d",
    "е" -> "e",
    "ё" -> "yo",
    "ж" -> "zh",
    "з" -> "z",
    "и" -> "i",
    "й" -> "y",
    "к" -> "k",
    "л" -> "l",
    "м" -> "m",
    "н" -> "n",
    "о" -> "o",
    "п" -> "p",
    "р" -> "r",
    "с" -> "s",
    "т" -> "t",
    "у" -> "u",
    "ф" -> "f",
    "х" -> "h",
    "ц" -> "tz",
    "ч" -> "ch",
    "ш" -> "sh",
    "щ" -> "sch",
    "ъ" -> "\'",
    "ы" -> "y",
    "ь" -> "\'",
    "э" -> "e",
    "ю" -> "yu",
    "я" -> "ya"
  )

  def transliterate(ruString: String) = ruString.foldLeft("")((s, c) => s + dictionary.get(c.toString).getOrElse(c.toString))
}

trait TranslitMapHolder {
  def translitMap: Seq[(String, Seq[Seq[String]])]
}

object V1TranslitMap extends TranslitMapHolder {
  val translitMap = Seq(
    ("ого", Seq(Seq("o",  "g", "o"),  Seq("o", "w", "o"))),
    ("его", Seq(Seq("e",  "g", "o"),  Seq("e", "w", "o"))),
    ("ге" , Seq(Seq("gu", "e"),       Seq("g", "e"))),
    ("пп" , Seq(Seq("p", "p"),       Seq("p", ""))),
    ("гия", Seq(Seq("gu", "i", "a"),  Seq("g", "i", "a"), Seq("gu", "i", "ya"), Seq("g", "i", "ya"))),
    ("ги" , Seq(Seq("gu", "i"),       Seq("g", "i"))),
    ("ия" , Seq(Seq("i",  "a"),       Seq("i", "ya"),     Seq("yi", "a"))),
    ("ий" , Seq(Seq("i",  "y"),       Seq("i", "j"),      Seq("",   "y"),       Seq("i", "i"),   Seq("y", "y"))),
    ("ый" , Seq(Seq("i",  "y"),       Seq("i", "j"),      Seq("",   "y"))),
    ("кс" , Seq(Seq("k",  "s"),       Seq("x", ""))),
    ("ъе" , Seq(Seq("j", "e"),        Seq("\"", "e"),     Seq("", "e"),         Seq("", "ye"))),
    ("ъё" , Seq(Seq("j", "e"),        Seq("\"", "e"),     Seq("\"", "yo"),      Seq("\"", "jo"), Seq("\"", "je"), Seq("\"", "ye"), Seq("j", "yo"), Seq("j", "ye"), Seq("", "yo"), Seq("", "jo"), Seq("", "je"), Seq("", "ye"))),
    ("ье" , Seq(Seq("j", "e"),        Seq("\'", "e"),     Seq("\'", "ye"),      Seq("\'", "je"), Seq("", "ie"),   Seq("i", "je"),  Seq("i", "ye"), Seq("j", "ye"))),
    ("ьи" , Seq(Seq("\'", "i"),       Seq("", "i"),       Seq("", "yi"),        Seq("\'", "yi"))),
    ("ьо" , Seq(Seq("\'", "o"),       Seq("", "io"),      Seq("", "o"))),
    ("ью" , Seq(Seq("\'", "yu"),      Seq("\'", "u"),     Seq("\'", "ju"),      Seq("", "ju"),   Seq("", "iu"),   Seq("\'", "iu"), Seq("", "yu"),  Seq("", "u"))),
    ("ья" , Seq(Seq("\'", "ya"),      Seq("\'", "ia"),    Seq("", "ya"),        Seq("", "ia"),   Seq("\'", "ja"), Seq("", "ja"))),
    ("а"  , Seq(Seq("a"))),
    ("б"  , Seq(Seq("b"))),
    ("в"  , Seq(Seq("v"),             Seq("w"))),
    ("г"  , Seq(Seq("g"))),
    ("д"  , Seq(Seq("d"))),
    ("е"  , Seq(Seq("e"),             Seq("je"),          Seq("ye"),            Seq("ie"))),
    ("ё"  , Seq(Seq("e"),             Seq("yo"),          Seq("jo"),            Seq("ye"),       Seq("o"))),
    ("ж"  , Seq(Seq("zh"),            Seq("j"),           Seq("sch"),           Seq("sh"),       Seq("g"))),
    ("з"  , Seq(Seq("z"),             Seq("s"))),
    ("и"  , Seq(Seq("i"),             Seq("yi"))),
    ("й"  , Seq(Seq("j"),             Seq("jj"),          Seq("y"),             Seq("i"))),
    ("к"  , Seq(Seq("k"))),
    ("л"  , Seq(Seq("l"))),
    ("м"  , Seq(Seq("m"))),
    ("н"  , Seq(Seq("n"))),
    ("о"  , Seq(Seq("o"))),
    ("п"  , Seq(Seq("p"))),
    ("р"  , Seq(Seq("r"))),
    ("с"  , Seq(Seq("s"))),
    ("т"  , Seq(Seq("t"))),
    ("у"  , Seq(Seq("u"))),
    ("ф"  , Seq(Seq("f"),             Seq("ph"))),
    ("х"  , Seq(Seq("h"),             Seq("kh"),          Seq("ch"))),
    ("ц"  , Seq(Seq("tz"),            Seq("z"),           Seq("c"),             Seq("cz"))),
    ("ч"  , Seq(Seq("ch"),            Seq("tsch"),        Seq("tch"))),
    ("ш"  , Seq(Seq("sh"),            Seq("sch"),         Seq("ch"))),
    ("щ"  , Seq(Seq("shch"),          Seq("sc"),          Seq("schtsch"),       Seq("stsch"),    Seq("chtch"),     Seq("sch"))),
    ("ъ"  , Seq(Seq("\""),            Seq(""),            Seq("j"))),
    ("ы"  , Seq(Seq("y"),             Seq("ui"))),
    ("ь"  , Seq(Seq("\'"))),
    ("э" , Seq(Seq("e"))),
    ("ю"  , Seq(Seq("yu"),            Seq("iu"), Seq("ju"), Seq("u"))),
    ("я"  , Seq(Seq("ya"),            Seq("ia"), Seq("ja")))
  )
}

object V2TranslitMap extends TranslitMapHolder {
  val translitMap = Seq(
    ("алексей", Seq(Seq("a", "l", "e", "x", "e", "y"),  Seq("a", "l", "e", "x", "e", "i"), Seq("a", "l", "e", "ks", "e", "y"), Seq("a", "l", "e", "ks", "e", "i"))),
    ("ого", Seq(Seq("o",  "g", "o"),  Seq("o", "w", "o"))),
    ("его", Seq(Seq("e",  "g", "o"),  Seq("e", "w", "o"))),
    ("ге" , Seq(Seq("gu", "e"),       Seq("g", "e"))),
    ("пп" , Seq(Seq("p", "p"),       Seq("p", ""))),
    ("др" , Seq(Seq("d", "r"),       Seq("p", ""))),
    ("гия", Seq(Seq("gu", "i", "a"),  Seq("g", "i", "a"), Seq("gu", "i", "ya"), Seq("g", "i", "ya"))),
    ("ги" , Seq(Seq("gu", "i"),       Seq("g", "i"))),
    ("ия" , Seq(Seq("i",  "a"),       Seq("i", "ya"),     Seq("yi", "a"))),
    ("ий" , Seq(Seq("i",  "y"),       Seq("i", "j"),      Seq("",   "y"),       Seq("i", "i"),   Seq("y", "i"))),
    ("ый" , Seq(Seq("i",  "y"),       Seq("i", "j"),      Seq("",   "y"),       Seq("i", "i"),   Seq("y", "i"))),
    ("кс" , Seq(Seq("k",  "s"),       Seq("x", ""))),
    ("ье" , Seq(Seq("j", "e"),        Seq("\'", "e"),     Seq("\'", "ye"),      Seq("\'", "je"), Seq("", "ie"),   Seq("i", "je"),  Seq("i", "ye"), Seq("j", "ye"))),
    ("ьи" , Seq(Seq("\'", "i"),       Seq("", "i"),       Seq("", "yi"),        Seq("\'", "yi"))),
    ("ьо" , Seq(Seq("\'", "o"),       Seq("", "io"),      Seq("", "o"))),
    ("ью" , Seq(Seq("\'", "yu"),      Seq("\'", "u"),     Seq("\'", "ju"),      Seq("", "ju"),   Seq("", "iu"),   Seq("\'", "iu"), Seq("", "yu"),  Seq("", "u"))),
    ("ья" , Seq(Seq("\'", "ya"),      Seq("\'", "ia"),    Seq("", "ya"),        Seq("", "ia"),   Seq("\'", "ja"), Seq("", "ja"))),
    ("а"  , Seq(Seq("a"))),
    ("б"  , Seq(Seq("b"))),
    ("в"  , Seq(Seq("v"),             Seq("w"))),
    ("г"  , Seq(Seq("g"))),
    ("д"  , Seq(Seq("d"))),
    ("е"  , Seq(Seq("e"),             Seq("je"),          Seq("ye"),            Seq("ie"))),
    ("ё"  , Seq(Seq("e"),             Seq("yo"),          Seq("jo"),            Seq("io"))),
    ("ж"  , Seq(Seq("zh"),            Seq("j"),           Seq("g"))),
    ("з"  , Seq(Seq("z"),             Seq("s"))),
    ("и"  , Seq(Seq("i"),             Seq("yi"))),
    ("й"  , Seq(Seq("j"),             Seq("y"),             Seq("i"))),
    ("к"  , Seq(Seq("k"))),
    ("л"  , Seq(Seq("l"))),
    ("м"  , Seq(Seq("m"))),
    ("н"  , Seq(Seq("n"))),
    ("о"  , Seq(Seq("o"))),
    ("п"  , Seq(Seq("p"))),
    ("р"  , Seq(Seq("r"))),
    ("с"  , Seq(Seq("s"))),
    ("т"  , Seq(Seq("t"))),
    ("у"  , Seq(Seq("u"),             Seq("ou"))),
    ("ф"  , Seq(Seq("f"),             Seq("ph"))),
    ("х"  , Seq(Seq("h"),             Seq("kh"))),
    ("ц"  , Seq(Seq("ts"),            Seq("tz"),           Seq("z"),             Seq("c"))),
    ("ч"  , Seq(Seq("ch"),            Seq("tsch"),        Seq("tch"))),
    ("ш"  , Seq(Seq("sh"),            Seq("sch"),         Seq("ch"))),
    ("щ"  , Seq(Seq("shch"),          Seq("sc"),          Seq("chtch"))),
    ("ъ"  , Seq(Seq("\""),            Seq(""),            Seq("j"))),
    ("ы"  , Seq(Seq("y"),             Seq("ui"))),
    ("ь"  , Seq(Seq("\'"),            Seq(""),            Seq("’"))),
    ("э" , Seq(Seq("e"))),
    ("ю"  , Seq(Seq("yu"),            Seq("iu"),          Seq("u"),               Seq("ju"))),
    ("я"  , Seq(Seq("ya"),            Seq("ia"),          Seq("ja")))
  )
}

// Be Careful with large amounts of text! A lot of variants possible
trait RUSmartTransliterationHelper extends Transliterator with JavaTokenParsers with Loggable {
  lazy val translitMapHolder: TranslitMapHolder = V2TranslitMap

  lazy val wordParser: Parser[List[(String, Seq[Seq[String]])]] = {
    val p = translitMapHolder.translitMap.map{case(k, v) => {
      val letterParser: Parser[(String, Seq[Seq[String]])] = k ^^ (m => (m, v))
      letterParser
    }}
    val anyCharParser = ".".r ^^ (t => (t, Seq(Seq(t))))
    rep(p.tail.foldLeft(p.head)(_ | _) | anyCharParser)
  }

  def transliterate(input: String): String = {
    Logger.debug(s"Transliterating: $input")
    parseWord(input).headOption.getOrElse(input)
  }

  def transliterationVariants(input: String): Seq[String] = {
    parseWord(input)
  }


  private def handleCase(parseResult: Seq[Seq[String]], originalWord: String): Seq[Seq[String]] = {
    val lowercasedOriginalWord = originalWord.toLowerCase
    var t = parseResult
    lowercasedOriginalWord.zipWithIndex.foreach{case(c, idx) => {
      if (idx < originalWord.length-1) {
        if (c.toUpper == originalWord.charAt(idx) && lowercasedOriginalWord.charAt(idx+1).toUpper == originalWord.charAt(idx+1)) {
          t = t.map(ct => {ct.slice(0, idx) :+ ct(idx).toUpperCase} ++ ct.slice(idx+1, ct.size))
        } else if (c.toUpper == originalWord.charAt(idx)) {
          t = t.map(ct => {ct.slice(0, idx) :+ ct(idx).capitalize} ++ ct.slice(idx+1, ct.size))
        }  else {}
      } else if (idx > 0) {
        if (c.toUpper == originalWord.charAt(idx) && lowercasedOriginalWord.charAt(idx-1).toUpper == originalWord.charAt(idx-1)) {
          t = t.map(ct => {ct.slice(0, idx) :+ ct(idx).toUpperCase})
        }
        else if (c.toUpper == originalWord.charAt(idx)) {
          t = t.map(ct => {ct.slice(0, idx) :+ ct(idx).capitalize})
        }  else {}
      } else {
        if (c.toUpper == originalWord.charAt(idx)) {
          t = t.map(ct => Seq(ct(idx).toUpperCase)) ++ t.filter(_.apply(idx).length>1).map(ct => Seq(ct.apply(idx).capitalize))
        }
        else {}
      }
    }}
    t
  }

  private def linearize(previous: Seq[String], position: Int, all: Seq[(String, Seq[Seq[String]])]): Seq[Seq[String]] = {
    if (position == all.length) Seq(previous)
    else {
      val vars = all(position)
      vars._2.flatMap(t => linearize(previous ++ t, position+1, all))
    }
  }


  def parseWord(word: String) = {
    parse(wordParser, word.toLowerCase) match {
      case Success(parsed, _) => {
        val linearized = linearize(Nil, 0, parsed)
        val allVars = handleCase(linearized, word).map(_.mkString(""))
        Logger.debug(s"Transliterated $word to $allVars variants")
        allVars
      }
      case e: NoSuccess => {
        //TODO: quiet error here - just return input word. Is it ok?
        Logger.error(s"Transliteration error happened: ${e.msg}")
        Seq(word)
      }
    }
  }
}