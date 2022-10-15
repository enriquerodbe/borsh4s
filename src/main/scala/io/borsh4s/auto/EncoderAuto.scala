package io.borsh4s.auto

import io.borsh4s.Encoder
import java.nio.ByteBuffer
import magnolia1.*

object EncoderAuto extends AutoDerivation[Encoder]:
  override def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (buffer, t) =>
      ctx.params.foreach(p => p.typeclass.encode(buffer, p.deref(t)))

  override def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (buffer, t) =>
      ctx.choose(t) { sub =>
        val _ = buffer.put(sub.subtype.index.toByte)
        sub.typeclass.encode(buffer, sub.cast(t))
      }
