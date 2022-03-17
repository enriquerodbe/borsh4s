package io.borsh4s.instances

import io.borsh4s
import io.borsh4s.Implicits.*
import munit.FunSuite
import scala.scalajs.js

class EncodersSpec extends FunSuite {
  test("byteEncoder") {
    val obtained = borsh4s.encode[Byte](15)
    val expected = Array[Byte](15)
    assert(obtained.sameElements(expected))
  }

  test("booleanEncoder - true") {
    val obtained = borsh4s.encode[Boolean](true)
    val expected = Array[Byte](1, 0, 0, 0)
    assert(obtained.sameElements(expected))
  }

  test("booleanEncoder - false") {
    val obtained = borsh4s.encode[Boolean](false)
    val expected = Array[Byte](0, 0, 0, 0)
    assert(obtained.sameElements(expected))
  }

  test("shortEncoder") {
    val obtained = borsh4s.encode[Short](514)
    val expected = Array[Byte](2, 2)
    assert(obtained.sameElements(expected))
  }

  test("intEncoder") {
    val obtained = borsh4s.encode[Int](257)
    val expected = Array[Byte](1, 1, 0, 0)
    assert(obtained.sameElements(expected))
  }

  test("longEncoder") {
    val obtained = borsh4s.encode[Long](Long.MaxValue)
    val expected = Array[Byte](-1, -1, -1, -1, -1, -1, -1, 127)
    assert(obtained.sameElements(expected))
  }

  test("floatEncoder") {
    val obtained = borsh4s.encode[Float](1f)
    val expected = Array[Byte](0, 0, -128, 63)
    assert(obtained.sameElements(expected))
  }

  test("doubleEncoder") {
    val obtained = borsh4s.encode[Double](1d)
    val expected = Array[Byte](0, 0, 0, 0, 0, 0, -16, 63)
    assert(obtained.sameElements(expected))
  }

  test("stringEncoder") {
    val obtained = borsh4s.encode[String]("Hello")
    val expected = Array[Byte](5, 0, 0, 0, 'H', 'e', 'l', 'l', 'o')
    assert(obtained.sameElements(expected))
  }

  test("dateEncoder") {
    val obtained = borsh4s.encode[js.Date](new js.Date(2038, 2, 15, 12, 21, 28))
    val expected = Array[Byte](0, 46, 107, -110, 124, -91, 7, 0)
    assert(obtained.sameElements(expected))
  }

  test("arrayEncoder") {
    val obtained = borsh4s.encode[Array[Byte]](Array(1, 2, 3, 4))
    val expected = Array[Byte](4, 0, 0, 0, 1, 2, 3, 4)
    assert(obtained.sameElements(expected))
  }

  test("mapEncoder") {
    val obtained = borsh4s.encode[Map[String, Array[Byte]]](
      Map("key1" -> Array(1, 2), "key2" -> Array(2, 1))
    )
    val expected = Array[Byte](2, 0, 0, 0, 4, 0, 0, 0, 'k', 'e', 'y', '1', 2, 0,
      0, 0, 1, 2, 4, 0, 0, 0, 'k', 'e', 'y', '2', 2, 0, 0, 0, 2, 1)
    assert(obtained.sameElements(expected))
  }
}
