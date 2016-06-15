package org.libss.logic.i18n

import java.util.Locale

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Kaa 
  * on 10.06.2016 at 00:09.
  */
case class LocalizedExample(locale: Locale) extends Localizable {
  val localeProvider = new LocaleProvider {
    override def getLocale: Locale = locale
  }
}

class RBSpec extends FlatSpec with Matchers {
  "Non-present property" should "return unchanged from getString" in {
    val ex = LocalizedExample(Locale.ROOT)
    ex.getString("qwe") shouldBe "qwe"
    ex.getString("prop.prop") shouldBe "prop.prop"
    ex.getString("я") shouldBe "я"
  }

  "Resource bundle" should "be found correctly" in {
    val lp = LocalizedExample(Locale.US)
    lp.getString("test.me") shouldBe "test me en"
  }

  "present property" should "be translated to property value from resource bundle on proper Language" in {
    val lp = LocalizedExample(new Locale("ru", "RU"))
    lp.getString("test.me") shouldBe "тестируй меня"
  }

  "For default locale" should "be used default bundle" in {
    val lp = LocalizedExample(Locale.ROOT)
    lp.getString("test.me") shouldBe "default test me"
  }
}

