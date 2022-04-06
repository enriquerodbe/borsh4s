package io.borsh4s.auto

import io.borsh4s.BinarySize
import shapeless.*

trait BinarySizeDerivation {
  implicit val hNilSize: BinarySize[HNil] = _ => 0

  implicit def hListSize[H, T <: HList](implicit
      hSize: Lazy[BinarySize[H]],
      tSize: BinarySize[T]
  ): BinarySize[H :: T] = { case h :: t =>
    hSize.value.calculate(h) + tSize.calculate(t)
  }

  implicit def genericSize[A, R](implicit
      generic: Generic.Aux[A, R],
      size: Lazy[BinarySize[R]]
  ): BinarySize[A] =
    a => size.value.calculate(generic.to(a))
}
