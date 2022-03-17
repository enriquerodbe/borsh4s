package io.borsh4s.auto

import io.borsh4s
import munit.FunSuite
import io.borsh4s.Implicits.*

class DerivationSpec extends FunSuite {

  case class MyTestClass(field1: Int, field2: String, nested: NestedClass)
  @SuppressWarnings(Array("org.wartremover.warts.ArrayEquals"))
  case class NestedClass(field: Array[Boolean])

  test("EncoderDerivation") {
    val instance = MyTestClass(1, "World", NestedClass(Array(true, false)))
    val obtained = borsh4s.encode(instance)
    val expected = Array[Byte](1, 0, 0, 0, 5, 0, 0, 0, 'W', 'o', 'r', 'l', 'd',
      2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0)
    assert(obtained.sameElements(expected))
  }

  test("DecoderDerivation") {
    val expected = MyTestClass(1, "World", NestedClass(Array(true, false)))
    val obtained = borsh4s.decode[MyTestClass](
      Array[Byte](1, 0, 0, 0, 5, 0, 0, 0, 'W', 'o', 'r', 'l', 'd', 2, 0, 0, 0,
        1, 0, 0, 0, 0, 0, 0, 0)
    )
    assertEquals(obtained.field1, expected.field1)
    assertEquals(obtained.field2, expected.field2)
    assert(obtained.nested.field.sameElements(expected.nested.field))
  }
}
