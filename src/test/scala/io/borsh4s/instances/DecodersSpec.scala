package io.borsh4s.instances

import io.borsh4s.{Borsh4s, given}
import munit.FunSuite

class DecodersSpec extends FunSuite:
  test("byteDecoder") {
    val obtained = Borsh4s.decode[Byte](Array[Byte](15))
    val expected: Byte = 15
    assertEquals(obtained, expected)
  }

  test("booleanDecoder - true") {
    val obtained = Borsh4s.decode[Boolean](Array[Byte](0x1))
    val expected: Boolean = true
    assertEquals(obtained, expected)
  }

  test("booleanDecoder - false") {
    val obtained = Borsh4s.decode[Boolean](Array[Byte](0x0))
    val expected: Boolean = false
    assertEquals(obtained, expected)
  }

  test("shortDecoder") {
    val obtained = Borsh4s.decode[Short](Array[Byte](2, 2))
    val expected: Short = 514
    assertEquals(obtained, expected)
  }

  test("intDecoder") {
    val obtained = Borsh4s.decode[Int](Array[Byte](1, 1, 0, 0))
    val expected: Int = 257
    assertEquals(obtained, expected)
  }

  test("longDecoder") {
    val obtained =
      Borsh4s.decode[Long](Array[Byte](-1, -1, -1, -1, -1, -1, -1, 127))
    val expected = Long.MaxValue
    assertEquals(obtained, expected)
  }

  test("floatDecoder") {
    val obtained = Borsh4s.decode[Float](Array[Byte](0, 0, -128, 63))
    val expected = 1f
    assertEquals(obtained, expected)
  }

  test("doubleDecoder") {
    val obtained =
      Borsh4s.decode[Double](Array[Byte](0, 0, 0, 0, 0, 0, -16, 63))
    val expected = 1d
    assertEquals(obtained, expected)
  }

  test("stringDecoder") {
    val obtained =
      Borsh4s.decode[String](Array[Byte](5, 0, 0, 0, 'H', 'e', 'l', 'l', 'o'))
    val expected = "Hello"
    assertEquals(obtained, expected)
  }

  test("optionDecoder - Some") {
    val obtained =
      Borsh4s.decode[Option[String]](Array[Byte](1, 2, 0, 0, 0, 'H', 'i'))
    val expected = Some("Hi")
    assertEquals(obtained, expected)
  }

  test("optionDecoder - None") {
    val obtained = Borsh4s.decode[Option[String]](Array[Byte](0))
    val expected = None
    assertEquals(obtained, expected)
  }

  test("arrayDecoder") {
    val obtained = Borsh4s.decode[Array[Byte]](Array(4, 0, 0, 0, 1, 2, 3, 4))
    val expected = Array[Byte](1, 2, 3, 4)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("setDecoder") {
    val obtained = Borsh4s.decode[Set[String]](
      Array(3, 0, 0, 0, 3, 0, 0, 0, 'a', 'n', 'y', 3, 0, 0, 0, 'A', 'n', 'y', 3,
        0, 0, 0, '1', '2', '3')
    )
    val expected = Set("Any", "any", "123")
    assertEquals(obtained, expected)
  }

  test("mapDecoder") {
    val obtained = Borsh4s.decode[Map[String, String]](
      Array[Byte](2, 0, 0, 0, 4, 0, 0, 0, 'k', 'e', 'y', '1', 2, 0, 0, 0, '1',
        '2', 4, 0, 0, 0, 'k', 'e', 'y', '2', 2, 0, 0, 0, '2', '1')
    )
    val expected = Map[String, String]("key1" -> "12", "key2" -> "21")
    assertEquals(obtained, expected)
  }
