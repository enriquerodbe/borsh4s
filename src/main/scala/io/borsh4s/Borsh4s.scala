package io.borsh4s

import java.nio.{ByteBuffer, ByteOrder}

object Borsh4s:
  def encode[T](
      t: T
  )(using encoder: Encoder[T], binarySize: BinarySize[T]): Array[Byte] =
    val size = binarySize.calculate(t)
    val buffer = ByteBuffer.allocate(size.toInt).order(ByteOrder.LITTLE_ENDIAN)
    encoder.encode(buffer, t)
    buffer.array()

  def decode[T](bytes: Array[Byte])(using
      decoder: Decoder[T]
  ): Decoder.Result[T] =
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    decoder.decode(buffer)

export instances.BinarySizes.given
export instances.Decoders.given
export instances.Encoders.given
export auto.BinarySizeAuto.autoDerived as binarySizeDerived
export auto.DecoderAuto.autoDerived as decoderDerived
export auto.EncoderAuto.autoDerived as encoderDerived
