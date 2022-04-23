package io

import java.nio.{ByteBuffer, ByteOrder}

package object borsh4s {
  def encode[T](t: T)(implicit
      encoder: Encoder[T],
      binarySize: BinarySize[T]
  ): Array[Byte] = {
    val size = binarySize.calculate(t)
    val buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN)
    encoder.encode(buffer, t)
    buffer.array()
  }

  def decode[T](bytes: Array[Byte])(implicit decoder: Decoder[T]): T =
    decoder.decode(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN))
}
