package io

import java.nio.{ByteBuffer, ByteOrder}

package object borsh4s {
  def encode[T](t: T)(implicit encoder: Encoder[T]): Array[Byte] = {
    val buffer = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN)
    encoder.encode(buffer, t)
    buffer.array().slice(0, buffer.position())
  }

  def decode[T](bytes: Array[Byte])(implicit decoder: Decoder[T]): T =
    decoder.decode(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN))
}
