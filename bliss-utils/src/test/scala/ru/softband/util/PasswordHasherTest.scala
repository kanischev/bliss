package ru.softband.util

import org.scalatest.{FlatSpec, Matchers}
import ru.softband.util.helpers.PasswordHasher

/**
 * date: 10.04.2014 21:52
 * author: Kaa
 */

class PasswordHasherTest
  extends FlatSpec
    with Matchers
    with PasswordHasher {

  val DefaultAdminSalt = "f693f8bf-80ff-448c-8c76-41532fd3847e"

  "Hashed without salt" should "be done correctly" in {
    generateHash("admin", None) shouldBe "c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec"
  }

  "Hashed with salt" should "be done correctly" in {
    generateHash("admin", Some(DefaultAdminSalt)) shouldBe "1ce8b5018391457553ea90ac61e6e900303d243e9eb6310bde66b7a33adf4fb471892fbbebddac06d6b49a06b9bc0b690d363ec877a8a88517cc713d308ce155"
  }
}
