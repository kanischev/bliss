package ru.softband.util.helpers

/**
  * date: 02.06.2016 22:44
  * author: Kaa
  *
  * This class is used for internal transparent Color representation conversions
  */

case class Color(red: Int, green: Int, blue: Int, opacity: Double = 1.0) {
  private val hexFormat = "%02x"
  def toHexString = hexFormat.format(red) + hexFormat.format(green) + hexFormat.format(blue)
  def toRackPrefixedHexString = "#" + toHexString
  def toSingleInt = Integer.parseInt(toHexString, 16)
}

object Color {
  def apply(hexString: String): Color = {
    if (hexString.startsWith("#")) apply(hexString.drop(1))
    else {
      if (hexString.length != 6) throw new IllegalArgumentException("Unknown color string")
      val (r, g, b) = (Integer.parseInt(hexString.substring(0, 2), 16),
        Integer.parseInt(hexString.substring(2, 4), 16),
        Integer.parseInt(hexString.substring(4, 6), 16))
      apply(r, g, b)
    }
  }
  def apply(rgbInt: Int): Color = {
    apply("#%06x".format(rgbInt))
  }
}
