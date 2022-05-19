package io.borsh4s.auto

import io.borsh4s
import munit.FunSuite
import io.borsh4s.Implicits.*

class DerivationSpec extends FunSuite {

  sealed trait MySealedTrait
  case class MyTestClassA(field: Boolean) extends MySealedTrait
  case class MyTestClassB(
      field1: Int,
      field2: Option[String],
      nested: NestedClass
  ) extends MySealedTrait
  @SuppressWarnings(Array("org.wartremover.warts.ArrayEquals"))
  case class NestedClass(field: Array[Boolean])
  case class MyTestClassC(field: Int) extends MySealedTrait
  case object MyTestObject extends MySealedTrait

  test("EncoderDerivation") {
    val instance =
      MyTestClassB(1, Some("World"), NestedClass(Array(true, false)))
    val obtained = borsh4s.encode[MySealedTrait](instance)
    val expected = Array[Byte](1, 1, 0, 0, 0, 1, 5, 0, 0, 0, 'W', 'o', 'r', 'l',
      'd', 2, 0, 0, 0, 1, 0)
    assertEquals(obtained.toSeq, expected.toSeq)
  }

  test("DecoderDerivation") {
    val expected =
      MyTestClassB(1, Some("World"), NestedClass(Array(true, false)))
    val obtained =
      borsh4s.decode[MySealedTrait](
        Array[Byte](1, 1, 0, 0, 0, 1, 5, 0, 0, 0, 'W', 'o', 'r', 'l', 'd', 2, 0,
          0, 0, 1, 0)
      )

    obtained match {
      case b: MyTestClassB =>
        assertEquals(b.field1, expected.field1)
        assertEquals(b.field2, expected.field2)
        assert(b.nested.field.sameElements(expected.nested.field))

      case _ =>
        fail("Incorrect class decoded")
    }
  }
}
