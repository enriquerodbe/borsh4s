package io.borsh4s.auto

import io.borsh4s.Decoder
import io.borsh4s.Decoder.*
import magnolia1.*

object DecoderAuto extends AutoDerivation[Decoder]:
  override def join[T](ctx: CaseClass[Decoder, T]): Decoder[T] =
    bytes => ctx.constructMonadic(_.typeclass.decode(bytes))

  override def split[T](ctx: SealedTrait[Decoder, T]): Decoder[T] =
    bytes =>
      bytes
        .read(_.get())
        .map(_.toInt)
        .flatMap {
          case index if ctx.subtypes.indices.contains(index) =>
            ctx.subtypes(index).typeclass.decode(bytes)
          case index =>
            Left(Failure.InvalidUnionValue(index, ctx.typeInfo.full, bytes))
        }
