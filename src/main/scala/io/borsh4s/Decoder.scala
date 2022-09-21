package io.borsh4s

import java.nio.ByteBuffer

import magnolia1.*

trait Decoder[T] {
  def decode(bytes: ByteBuffer): T
}

object Decoder {
  type Typeclass[T] = Decoder[T]

  def join[T](ctx: CaseClass[Decoder, T]): Decoder[T] =
    (bytes: ByteBuffer) => ctx.construct(_.typeclass.decode(bytes))

  implicit def gen[T]: Decoder[T] = macro Magnolia.gen[T]
}
