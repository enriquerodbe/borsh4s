package io.borsh4s

import java.nio.ByteBuffer

trait Encoder[T]:
  def encode(buffer: ByteBuffer, t: T): Unit
