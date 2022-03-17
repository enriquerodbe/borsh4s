package io.borsh4s.auto

import io.borsh4s.Decoder
import shapeless.*

trait DecoderDerivation {
  implicit val hNilDecoder: Decoder[HNil] = _ => HNil

  implicit def hListDecoder[H, T <: HList](implicit
      hDecoder: Lazy[Decoder[H]],
      tDecoder: Decoder[T]
  ): Decoder[H :: T] =
    buffer => hDecoder.value.decode(buffer) :: tDecoder.decode(buffer)

  implicit def genericDecoder[A, R](implicit
      generic: Generic.Aux[A, R],
      decoder: Lazy[Decoder[R]]
  ): Decoder[A] =
    bytes => generic.from(decoder.value.decode(bytes))
}
