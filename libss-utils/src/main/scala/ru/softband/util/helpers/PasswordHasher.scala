package ru.softband.util.helpers

import java.security.MessageDigest
import java.util.UUID
import org.apache.commons.lang3.RandomStringUtils

/**
  * date: 02.06.2016 22:52
  * author: Kaa
  *
  * Some basic password hashing helper implementation
  */

trait PasswordHasher {
  private val DigestAlgorythm = "SHA-512"

  def generateSalt = UUID.randomUUID().toString

  def generateHash(input: String, salt: Option[String]): String =
    generateByteArrayHash((salt.map(generateHash(_, None)).getOrElse("") + input).getBytes)

  def generateByteArrayHash(data: Array[Byte]) = {
    val md = MessageDigest.getInstance(DigestAlgorythm)
    md.digest(data).map(0xFF & _).map("%02x".format(_)).foldLeft("")(_ + _)
  }
}

trait PasswordGenerator {
  def generatePassword(length: Int = 10) = {
    RandomStringUtils.randomAlphanumeric(length)
  }
}

/**
  * Md5 hasher helper
  */
trait MD5HexGenerator {
  private val md5 = "MD5"

  def generateMd5Hex(input: String): String = generateMd5Hex(input.getBytes)

  def generateMd5Hex(data: Array[Byte]) = {
    val md = MessageDigest.getInstance(md5)
    md.digest(data).map(0xFF & _).map("%02x".format(_)).foldLeft("")(_ + _)
  }
}