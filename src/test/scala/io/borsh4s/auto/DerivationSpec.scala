package io.borsh4s.auto

import io.borsh4s.{Borsh4s, given}
import io.borsh4s.Decoder.Failure
import munit.{Clues, FunSuite}

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

  test("EncoderDerivation - constant sizes") {
    final case class ConstantSizes(a: Int, b: Short)
    val obtained = Borsh4s.encode(ConstantSizes(15, 2))
    val expected = Array[Byte](15, 0, 0, 0, 2, 0)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("DecoderDerivation") {
    val obtained = Borsh4s.decode[MyEnum](binary)

    obtained match
      case Right(b: MyEnum.MyCaseB) =>
        assertEquals(b.field1, instance.field1)
        assertEquals(b.field2, instance.field2)
        assertEquals(b.nested.field.toSeq, instance.nested.field.toSeq)

      case other =>
        fail("Incorrect class decoded", Clues.fromValue(other))
  }

  test("DecoderDerivation - Invalid union value") {
    val obtained = Borsh4s.decode[MyEnum](Array[Byte](18))

    obtained match
      case Left(Failure.InvalidUnionValue(index, name, buffer)) =>
        assertEquals(index, 18)
        assertEquals(name, "io.borsh4s.auto.DerivationSpec.MyEnum")
        assertEquals(buffer.position(), 1)
        assertEquals(buffer.limit(), 1)
        assertEquals(buffer.capacity(), 1)
      case _ =>
        fail("Incorrect handling of invalid union value")
  }
