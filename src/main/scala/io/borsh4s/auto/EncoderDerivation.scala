package io.borsh4s.auto

import io.borsh4s.Encoder
import shapeless.*

trait EncoderDerivation {
  implicit val hNilEncoder: Encoder[HNil] = (_, _) => ()

  implicit def hListEncoder[H, T <: HList](implicit
      hEncoder: Lazy[Encoder[H]],
      tEncoder: Encoder[T]
  ): Encoder[H :: T] = { case (buffer, h :: t) =>
    hEncoder.value.encode(buffer, h)
    tEncoder.encode(buffer, t)
  }

  implicit def genericEncoder[A, R](implicit
      generic: Generic.Aux[A, R],
      encoder: Lazy[Encoder[R]]
  ): Encoder[A] =
    (buffer, a) => encoder.value.encode(buffer, generic.to(a))
}
