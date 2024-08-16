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
        .flatMap { index =>
          ctx.subtypes.find(_.index == index) match
            case Some(subtype) =>
              subtype.typeclass.decode(bytes)
            case None =>
              Left(Failure.InvalidUnionValue(index, ctx.typeInfo.full, bytes))
        }
