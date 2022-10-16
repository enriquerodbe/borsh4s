package io.borsh4s.instances

import io.borsh4s.Decoder
import java.nio.charset.StandardCharsets
import scala.reflect.ClassTag

object Decoders:
  given Decoder[Byte] = _.get

  given Decoder[Boolean] = _.get == 0x1

  given Decoder[Short] = _.getShort

  given Decoder[Int] = _.getInt

  given Decoder[Long] = _.getLong

  given Decoder[Float] = _.getFloat

  given Decoder[Double] = _.getDouble

  given Decoder[String] =
    buffer =>
      val length = buffer.getInt
      val byteArray = new Array[Byte](length)
      buffer.get(byteArray)
      new String(byteArray, StandardCharsets.UTF_8)

  given [T](using tDecoder: Decoder[T]): Decoder[Option[T]] =
    buffer =>
      buffer.get() match {
        case 0x0 => None
        case 0x1 => Some(tDecoder.decode(buffer))
      }

  given [T: ClassTag](using tDecoder: Decoder[T]): Decoder[Array[T]] =
    buffer =>
      val length = buffer.getInt
      (0 until length).map(_ => tDecoder.decode(buffer)).toArray

  given [T](using tDecoder: Decoder[T]): Decoder[Set[T]] =
    buffer =>
      val size = buffer.getInt
      (1 to size).map(_ => tDecoder.decode(buffer)).toSet

  given [K, V](using
      kDecoder: Decoder[K],
      vDecoder: Decoder[V]
  ): Decoder[Map[K, V]] =
    buffer =>
      val length = buffer.getInt
      (0 until length).map { _ =>
        val key = kDecoder.decode(buffer)
        val value = vDecoder.decode(buffer)
        (key, value)
      }.toMap
