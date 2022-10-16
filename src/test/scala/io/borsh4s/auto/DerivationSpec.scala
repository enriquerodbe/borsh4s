package io.borsh4s.auto

import io.borsh4s.{Borsh4s, given}
import munit.FunSuite

class DerivationSpec extends FunSuite:
  enum MyEnum {
    case MyCaseA(field: Boolean)
    case MyCaseB(field1: Int, field2: Option[String], nested: NestedClass)
    case MyCaseC(field: Int)
    case MyCaseD
  }
  case class NestedClass(field: Array[Boolean])

  val instance: MyEnum.MyCaseB =
    MyEnum.MyCaseB(1, Some("World"), NestedClass(Array(true, false)))
  val binary =
    Array[Byte](1, 1, 0, 0, 0, 1, 5, 0, 0, 0, 'W', 'o', 'r', 'l', 'd', 2, 0, 0,
      0, 1, 0)

  test("EncoderDerivation") {
    val obtained = Borsh4s.encode[MyEnum](instance)
    assertEquals(obtained.toSeq, binary.toSeq)
  }

  test("DecoderDerivation") {
    val obtained = Borsh4s.decode[MyEnum](binary)

    obtained match
      case b: MyEnum.MyCaseB =>
        assertEquals(b.field1, instance.field1)
        assertEquals(b.field2, instance.field2)
        assertEquals(b.nested.field.toSeq, instance.nested.field.toSeq)

      case _ =>
        fail("Incorrect class decoded")
  }
