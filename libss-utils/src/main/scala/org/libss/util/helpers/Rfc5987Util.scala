package org.libss.util.helpers

import java.io.{ByteArrayOutputStream, IOException, UnsupportedEncodingException}
import java.util.Arrays
import java.util.regex.{Matcher, Pattern}

/**
  * Created by Kaa 
  * on 03.06.2016 at 04:43.
  */
object Rfc5987Util {
  private val ENCODED_VALUE_PATTERN: Pattern = Pattern.compile("%[0-9a-f]{2}|\\S", Pattern.CASE_INSENSITIVE)
  private val DIGITS: Array[Char] = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
  private val ATTRIBUTE_CHARS: Array[Byte] = Array('!', '#', '$', '&', '+', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|', '~')

  def encode(s: String): String = {
    var encodedString: String = null
    try {
      encodedString = encode(s, "UTF-8")
    }
    catch {
      case e: UnsupportedEncodingException => e.printStackTrace()
    }

    encodedString
  }

  @throws(classOf[UnsupportedEncodingException])
  def encode(s: String, encoding: String): String = {
    val rawBytes: Array[Byte] = s.getBytes(encoding)
    val sb: StringBuilder = new StringBuilder(rawBytes.length << 1)
    for (b <- rawBytes) {
      if (Arrays.binarySearch(ATTRIBUTE_CHARS, b) >= 0) {
        sb.append(b.toChar)
      }
      else {
        sb.append('%')
        sb.append(DIGITS(0x0f & (b >>> 4)))
        sb.append(DIGITS(b & 0x0f))
      }
    }
    sb.toString
  }

  @throws(classOf[IOException])
  def decode(s: String, encoding: String): String = {
    val matcher: Matcher = ENCODED_VALUE_PATTERN.matcher(s)
    try {
      val bos: ByteArrayOutputStream = new ByteArrayOutputStream
      try {
        while (matcher.find) {
          val matched: String = matcher.group
          if (matched.startsWith("%")) {
            val value: Integer = Integer.parseInt(matched.substring(1), 16)
            bos.write(value)
          }
          else {
            bos.write(matched.charAt(0))
          }
        }
        new String(bos.toByteArray, encoding)
      } finally {
        if (bos != null) bos.close()
      }
    }
  }
}

trait ResponseUtils {
  def fileNameHeader(fileName: String) = "attachment; filename*=UTF-8''" + Rfc5987Util.encode(fileName)
}