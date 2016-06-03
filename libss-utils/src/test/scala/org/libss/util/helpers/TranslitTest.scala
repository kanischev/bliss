package org.libss.util.helpers

import org.scalatest.{FlatSpec, Matchers}

/**
 * date: 10.04.2014 21:54
 * author: Kaa
 */

class TranslitTest
  extends FlatSpec
    with Matchers
    with TransliterationHelper {
  "Transliteration from russian" should "be done correctly" in {
    transliterate("Садитесь, я вам рад!") should be("Sadites\', ya vam rad!")
  }
}

