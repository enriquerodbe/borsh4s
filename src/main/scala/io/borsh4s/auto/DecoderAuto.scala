package io.borsh4s.auto

import io.borsh4s.Decoder
import java.nio.ByteBuffer
import magnolia1.*

object DecoderAuto extends AutoDerivation[Decoder]:
  override def join[T](ctx: CaseClass[Decoder, T]): Decoder[T] =
    bytes => ctx.construct(_.typeclass.decode(bytes))

  override def split[T](ctx: SealedTrait[Decoder, T]): Decoder[T] =
    bytes =>
      val index = bytes.get().toInt
      ctx.subtypes(index).typeclass.decode(bytes)
