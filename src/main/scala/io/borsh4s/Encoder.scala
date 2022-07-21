package io.borsh4s

import java.nio.ByteBuffer
import magnolia1.*

trait Encoder[T] {
  def encode(buffer: ByteBuffer, t: T): Unit
}

object Encoder {
  def instance[T](e: (ByteBuffer, T) => ByteBuffer): Encoder[T] =
    (buffer: ByteBuffer, t: T) => {
      val _ = e(buffer, t) // Suppress "discarded non-Unit value" warning
    }

  type Typeclass[T] = Encoder[T]

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (buffer: ByteBuffer, t: T) =>
      ctx.parameters.foreach(p => p.typeclass.encode(buffer, p.dereference(t)))

  def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (buffer: ByteBuffer, t: T) =>
      ctx.split(t) { sub =>
        val _ = buffer.put(sub.index.toByte)
        sub.typeclass.encode(buffer, sub.cast(t))
      }

  implicit def gen[T]: Encoder[T] = macro Magnolia.gen[T]
}
