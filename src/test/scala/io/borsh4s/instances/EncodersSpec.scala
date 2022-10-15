package io.borsh4s.instances

import io.borsh4s.{Borsh4s, given}
import munit.FunSuite

class EncodersSpec extends FunSuite:
  test("byteEncoder") {
    val obtained = Borsh4s.encode[Byte](15)
    val expected = Array[Byte](15)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("booleanEncoder - true") {
    val obtained = Borsh4s.encode[Boolean](true)
    val expected = Array[Byte](0x1)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("booleanEncoder - false") {
    val obtained = Borsh4s.encode[Boolean](false)
    val expected = Array[Byte](0x0)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("shortEncoder") {
    val obtained = Borsh4s.encode[Short](514)
    val expected = Array[Byte](2, 2)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("intEncoder") {
    val obtained = Borsh4s.encode[Int](257)
    val expected = Array[Byte](1, 1, 0, 0)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("longEncoder") {
    val obtained = Borsh4s.encode[Long](Long.MaxValue)
    val expected = Array[Byte](-1, -1, -1, -1, -1, -1, -1, 127)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("floatEncoder") {
    val obtained = Borsh4s.encode[Float](1f)
    val expected = Array[Byte](0, 0, -128, 63)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("doubleEncoder") {
    val obtained = Borsh4s.encode[Double](1d)
    val expected = Array[Byte](0, 0, 0, 0, 0, 0, -16, 63)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("stringEncoder") {
    val obtained = Borsh4s.encode[String]("Hello")
    val expected = Array[Byte](5, 0, 0, 0, 'H', 'e', 'l', 'l', 'o')
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("optionEncoder - Some") {
    val obtained = Borsh4s.encode[Option[Long]](Some(32L))
    val expected = Array[Byte](1, 32, 0, 0, 0, 0, 0, 0, 0)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("optionEncoder - None") {
    val obtained = Borsh4s.encode[Option[String]](None)
    val expected = Array[Byte](0)
    assertEquals(
      obtained.toSeq,
      expected.toSeq,
      s"${obtained.toSeq} | ${expected.toSeq}"
    )
  }

  test("arrayEncoder") {
    val obtained = Borsh4s.encode[Array[Byte]](Array(1, 2, 3, 4))
    val expected = Array[Byte](4, 0, 0, 0, 1, 2, 3, 4)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("setEncoder") {
    val obtained = Borsh4s.encode[Set[String]](Set("Any", "any", "123"))
    val expected = Array[Byte](3, 0, 0, 0, 3, 0, 0, 0, '1', '2', '3', 3, 0, 0,
      0, 'A', 'n', 'y', 3, 0, 0, 0, 'a', 'n', 'y')
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("mapEncoder") {
    val obtained = Borsh4s.encode[Map[String, Array[Byte]]](
      Map("key1" -> Array(1, 2), "key2" -> Array(2, 1))
    )
    val expected = Array[Byte](2, 0, 0, 0, 4, 0, 0, 0, 'k', 'e', 'y', '1', 2, 0,
      0, 0, 1, 2, 4, 0, 0, 0, 'k', 'e', 'y', '2', 2, 0, 0, 0, 2, 1)
    assertEquals(obtained.toSeq, expected.toSeq)
  }
