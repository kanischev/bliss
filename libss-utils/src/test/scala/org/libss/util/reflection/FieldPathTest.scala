package org.libss.util.reflection

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * date: 02.06.2016 23:20
  * author: Kaa
  *
  * Test for Field paths
  */


class FieldPathTest
  extends FlatSpec
    with Matchers
    with StringFieldPathConversionHelper {

  val NewV = "newValue"
  val InitV = "init"

  private case class SimpleFieldTestHelper(name: String = "init")
  private case class SimpleOptionFieldTestHelper(name: Option[String] = None)
  private case class SimpleMapFieldTestHelper(m: mutable.Map[String, Any])
  private case class NestedFieldTestHelper(field: SimpleFieldTestHelper)

  "Parsing of simple single field path" should "result in single SimpleFieldPathPart" in {
    FieldPathParser.parseFrom("name") shouldBe List(SimpleFieldPathPart("name"))
  }

  "Parsing of complex field path" should "result in sequence of different FieldPathParts" in {
    FieldPathParser.parseFrom("name.(value).[option]") shouldBe List(SimpleFieldPathPart("name"), MapFieldPathPart("value"), OptionFieldPathPart("option"))
  }

  "Object attribute set by simple 1st level field path" should "be changed by fieldpath provided setter" in {
    val fp = FieldPath[String]("name")
    fp.toPath should be("name")
    val sfpp = SimpleFieldTestHelper()
    fp.parts.head.getFrom(sfpp) shouldBe Some(InitV)
    fp.parts.head.setTo(sfpp, Option(NewV))
    fp.parts.head.getFrom(sfpp) shouldBe Some(NewV)
  }

  "Object optional attribute set by simple 1st level optional field path" should "be changed by fieldpath provided setter" in {
    val fp = FieldPath[String](Seq(OptionFieldPathPart("name")))
    fp.toPath shouldBe "[name]"
    val sfpp = SimpleOptionFieldTestHelper()
    fp.parts.head.getFrom(sfpp) shouldBe None
    fp.parts.head.setTo(sfpp, Option(NewV))
    fp.parts.head.getFrom(sfpp) shouldBe Some(NewV)
  }

  "Object mutable.Map key value set by simple 1st level map key field path" should "be changed by fieldpath provided setter" in {
    val fp = FieldPath[String](Seq(SimpleFieldPathPart("m"), MapFieldPathPart("name")))
    val smf = SimpleMapFieldTestHelper(mutable.Map("name" -> NewV))
    fp.toPath shouldBe "m.(name)"
    fp.parts.head.getFrom(smf).get shouldBe smf.m
    fp.parts.last.getFrom(smf.m) shouldBe Some(NewV)
    fp.parts.last.setTo(smf.m, Option("qwerty"))
    fp.parts.last.getFrom(smf.m) shouldBe Some("qwerty")
  }

  "Setter provided by complex field path" should "change nested field's value" in {
    val t = NestedFieldTestHelper(SimpleFieldTestHelper())
    val s = Seq(SimpleFieldPathPart("field"), SimpleFieldPathPart("name"))

    s.foldLeft(Option(t.asInstanceOf[Any]))((o, pathPart) => {
      o.flatMap(t => pathPart.getFrom(t.asInstanceOf[AnyRef]))
    }) should be(Some("init"))
  }
}
