package org.libss.logic.i18n

import java.util.Locale

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Kaa 
  * on 10.06.2016 at 00:09.
  */
case class LocalizedExample(locale: Locale) extends Localizable {
  val localeProvider = DefiniteLocaleProvider(locale)
}

class RBSpec extends FlatSpec with Matchers {
  "Non-present property" should "return unchanged from getString" in {
    val ex = LocalizedExample(Locale.ROOT)
    ex.i18n("qwe") shouldBe "qwe"
    ex.i18n("prop.prop") shouldBe "prop.prop"
    ex.i18n("я") shouldBe "я"
  }

  "Resource bundle" should "be found correctly" in {
    val lp = LocalizedExample(Locale.US)
    lp.i18n("test.me") shouldBe "test me en"
  }

  "present property" should "be translated to property value from resource bundle on proper Language" in {
    val lp = LocalizedExample(new Locale("ru", "RU"))
    lp.i18n("test.me") shouldBe "тестируй меня"
  }

  "For default locale" should "be used default bundle" in {
    val lp = LocalizedExample(Locale.ROOT)
    lp.i18n("test.me") shouldBe "default test me"
  }
}

