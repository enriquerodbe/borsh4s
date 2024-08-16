package io.borsh4s.instances

import io.borsh4s.Encoder
import java.nio.charset.StandardCharsets
import scala.annotation.nowarn

@nowarn("msg=discarded non-Unit value of type java.nio.ByteBuffer")
object Encoders:
  given Encoder[Byte] = _.put(_)

  given Encoder[Boolean] =
    (buffer, boolean) =>
      val value: Byte = if boolean then 0x1 else 0x0
      buffer.put(value)

  given Encoder[Short] = _.putShort(_)

  given Encoder[Int] = _.putInt(_)

  given Encoder[Long] = _.putLong(_)

  given Encoder[Float] = _.putFloat(_)

  given Encoder[Double] = _.putDouble(_)

  given Encoder[String] =
    (buffer, string) =>
      val bytes = string.getBytes(StandardCharsets.UTF_8)
      val length = bytes.length
      buffer.putInt(length)
      bytes.foreach(buffer.put)

  given [T](using tEncoder: Encoder[T]): Encoder[Option[T]] =
    case (buffer, Some(t)) =>
      buffer.put(0x1: Byte)
      tEncoder.encode(buffer, t)
    case (buffer, None) =>
      buffer.put(0x0: Byte)

  given [T](using tEncoder: Encoder[T]): Encoder[Array[T]] =
    (buffer, array) =>
      val length = array.length
      buffer.putInt(length)
      array.foreach(tEncoder.encode(buffer, _))

  given [T: Ordering](using tEncoder: Encoder[T]): Encoder[Set[T]] =
    (buffer, set) =>
      buffer.putInt(set.size)
      set.toSeq.sorted.foreach(tEncoder.encode(buffer, _))

  given [K: Ordering, V](using
      kEncoder: Encoder[K],
      vEncoder: Encoder[V]
  ): Encoder[Map[K, V]] =
    (buffer, map) =>
      val length = map.size
      val keyValues = map.toList.sortBy(_._1)
      buffer.putInt(length)
      keyValues.foreach:
        case (k, v) =>
          kEncoder.encode(buffer, k)
          vEncoder.encode(buffer, v)
