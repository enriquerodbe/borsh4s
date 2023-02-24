package io.borsh4s.instances

import io.borsh4s.BinarySize
import io.borsh4s.BinarySize.*
import io.borsh4s.BinarySize.{Constant, Function}
import io.borsh4s.Naturals.Nat
import io.borsh4s.Naturals.Nat.sumBy

object BinarySizes:
  given BinarySize[Byte] = Constant.One

  given BinarySize[Boolean] = Constant.One

  given BinarySize[Short] = Constant.Two

  given BinarySize[Int] = Constant.Four

  given BinarySize[Long] = Constant.Eight

  given BinarySize[Float] = Constant.Four

  given BinarySize[Double] = Constant.Eight

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  given BinarySize[String] =
    Function(str => Nat(str.length).get + Nat.Four)

  given [T: BinarySize]: BinarySize[Option[T]] =
    Function(opt => total(opt.toList) + Nat.One)

  given [T: BinarySize]: BinarySize[Array[T]] =
    Function(total(_) + Nat.Four)

  given [T: BinarySize]: BinarySize[Set[T]] =
    Function(total(_) + Nat.Four)

  given [K, V](using
      kSize: BinarySize[K],
      vSize: BinarySize[V]
  ): BinarySize[(K, V)] =
    (kSize, vSize) match
      case (Constant(kValue), Constant(vValue)) =>
        Constant(kValue + vValue)
      case _ =>
        Function((k, v) => kSize.calculate(k) + vSize.calculate(v))

  given [K, V](using
      kSize: BinarySize[K],
      vSize: BinarySize[V]
  ): BinarySize[Map[K, V]] =
    Function(total(_) + Nat.Four)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private def total[T](iter: Iterable[T])(using tSize: BinarySize[T]): Nat =
    tSize match
      case Constant(value)       => value * Nat(iter.size).get
      case function: Function[_] => iter.sumBy(function.calculate)
