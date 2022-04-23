package io.borsh4s.instances

import io.borsh4s.Encoder
import java.nio.charset.StandardCharsets

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
trait Encoders {
  implicit val byteEncoder: Encoder[Byte] = Encoder.instance(_.put(_))

  implicit val booleanEncoder: Encoder[Boolean] =
    Encoder.instance { (buffer, boolean) =>
      val value: Byte = if (boolean) 0x1 else 0x0
      buffer.put(value)
    }

  implicit val shortEncoder: Encoder[Short] = Encoder.instance(_.putShort(_))

  implicit val intEncoder: Encoder[Int] = Encoder.instance(_.putInt(_))

  implicit val longEncoder: Encoder[Long] = Encoder.instance(_.putLong(_))

  implicit val floatEncoder: Encoder[Float] = Encoder.instance(_.putFloat(_))

  implicit val doubleEncoder: Encoder[Double] = Encoder.instance(_.putDouble(_))

  implicit val stringEncoder: Encoder[String] = { (buffer, string) =>
    val bytes = string.getBytes(StandardCharsets.UTF_8)
    val length = bytes.length
    buffer.putInt(length)
    bytes.foreach(buffer.put)
  }

  implicit def optionEncoder[T](implicit
      tEncoder: Encoder[T]
  ): Encoder[Option[T]] = Encoder.instance {
    case (buffer, Some(t)) =>
      buffer.put(0x1: Byte)
      tEncoder.encode(buffer, t)
      buffer
    case (buffer, None) =>
      buffer.put(0x0: Byte)
  }

  implicit def arrayEncoder[T](implicit
      tEncoder: Encoder[T]
  ): Encoder[Array[T]] = { (buffer, array) =>
    val length = array.length
    buffer.putInt(length)
    array.foreach(tEncoder.encode(buffer, _))
  }

  implicit def setEncoder[T: Ordering](implicit
      tEncoder: Encoder[T]
  ): Encoder[Set[T]] = { (buffer, set) =>
    buffer.putInt(set.size)
    set.toSeq.sorted.foreach(tEncoder.encode(buffer, _))
  }

  implicit def mapEncoder[K: Ordering, V](implicit
      kEncoder: Encoder[K],
      vEncoder: Encoder[V]
  ): Encoder[Map[K, V]] = { (buffer, map) =>
    val length = map.size
    val keyValues = map.toList.sortBy(_._1)
    buffer.putInt(length)
    keyValues.foreach { case (k, v) =>
      kEncoder.encode(buffer, k)
      vEncoder.encode(buffer, v)
    }
  }
}
