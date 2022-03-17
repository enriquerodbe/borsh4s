package io.borsh4s

import java.nio.ByteBuffer

trait Encoder[T] {
  def encode(buffer: ByteBuffer, t: T): Unit
}

object Encoder {
  def instance[T](e: (ByteBuffer, T) => ByteBuffer): Encoder[T] =
    (buffer: ByteBuffer, t: T) => { val _ = e(buffer, t) }
}
