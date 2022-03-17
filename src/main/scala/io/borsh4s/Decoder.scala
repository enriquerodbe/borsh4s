package io.borsh4s

import java.nio.ByteBuffer

trait Decoder[T] {
  def decode(bytes: ByteBuffer): T
}
