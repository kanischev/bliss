package ru.softband.util

import org.scalatest.{FlatSpec, Matchers}
import ru.softband.util.helpers.RuTransliterationHelper

/**
 * date: 10.04.2014 21:54
 * author: Kaa
 */

class TranslitTest
  extends FlatSpec
    with Matchers
    with RuTransliterationHelper {
  "Transliteration from russian" should "be done correctly" in {
    transliterate("Садитесь, я вам рад!") should be("Sadites\', ya vam rad!")
  }
}

