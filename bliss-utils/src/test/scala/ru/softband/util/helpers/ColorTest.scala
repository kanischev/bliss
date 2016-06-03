package ru.softband.util.helpers

import org.scalatest.{FlatSpec, Matchers}

/**
  * date: 02.06.2016 23:11
  * author: Kaa
  *
  * Test for Color class conversions
  */
class ColorTest extends FlatSpec with Matchers {

  "Hex color strings" should "initialize Color class instances in the same manner as Int RGB representation" in {
    Color("1b9476") shouldBe Color(27, 148, 118)
    Color("#1b9476") shouldBe Color(27, 148, 118)
    Color("#FFFFFF") shouldBe Color(255, 255, 255)
    Color("#ffffff") shouldBe Color(255, 255, 255)
    Color("000000") shouldBe Color(0, 0, 0)
  }

  "Int RGB initialized color" should "be converted to proper hex strings" in {
    Color(27, 148, 118).toRackPrefixedHexString shouldBe "#1b9476"
    Color(27, 148, 118).toHexString shouldBe "1b9476"
    Color(0, 0, 0).toRackPrefixedHexString shouldBe "#000000"
    Color(255, 0, 0).toHexString shouldBe "ff0000"
    Color(0, 255, 0).toHexString shouldBe "00ff00"
    Color(0, 0, 255).toHexString shouldBe "0000ff"
  }
}
