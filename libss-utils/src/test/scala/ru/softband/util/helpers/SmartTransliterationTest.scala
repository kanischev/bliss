package ru.softband.util.helpers

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Kaa 
  * on 03.06.2016 at 04:58.
  */
class SmartTransliterationTest extends FlatSpec with Matchers with RUSmartTransliterationHelper {
  "No symbols" should "be replaced" in {
    val engWord = "hello"
    parseWord(engWord) should be (Seq(engWord))
  }

  "Привет transliterations" should "contain \'privet\', \'privyet\', \'privjet\' variants" in {
    parseWord("привет") should (contain ("privet") and contain ("privyet") and contain("privjet"))
  }

  "Uppercase \'ПРИВЕТ\' transliteration result" should "contain \'PRIVET\', \'PRIVYET\', \'PRIVJET\'" in {
    parseWord("ПРИВЕТ") should (contain ("PRIVET") and contain ("PRIVYET") and contain("PRIVJET"))
  }

  "Complex char cases for \'приВЕТ\' transliteration result" should "be properly handled" in {
    parseWord("приВЕТ") should (contain ("priVET") and contain ("priVYET") and contain("priVJET"))
    parseWord("приВЕт") should (contain ("priVEt") and contain ("priVYet") and contain("priVJet"))
  }

  "Complex char cases for single and few uppers" should "be properly handled" in {
    parseWord("технологИя") should (contain ("tekhnologIya") and contain ("tekhnologIa") and contain("tekhnologIa"))
    parseWord("технолоГия") should (contain ("tekhnoloGiya") and contain ("tekhnoloGuia"))
    parseWord("технолоГИя") should contain("tekhnoloGIa")
    parseWord("технолоГиЯ") should contain("tekhnoloGiYa")
  }

  "Single letter \'ц\'" should "be translated in all translit map variants" in {
    println(parseWord("ц"))
    parseWord("ц") should (contain ("tz") and contain ("c"))
    parseWord("ц").length should be(4)
  }

  "Name \'александр\' trasliteration results" should "contain proper variants" in {
    println(parseWord("александр"))
    parseWord("александр") should (contain ("alexandr") and contain ("alexandr") and not contain("alexsandr"))
    parseWord("ц").length should be(4)
  }

}
