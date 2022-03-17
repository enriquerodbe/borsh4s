package io.borsh4s.instances

import io.borsh4s.Encoder
import java.nio.charset.StandardCharsets
import scala.scalajs.js

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
trait Encoders {
  implicit val byteEncoder: Encoder[Byte] = Encoder.instance(_.put(_))

  implicit val booleanEncoder: Encoder[Boolean] = Encoder.instance {
    (buffer, boolean) => if (boolean) buffer.putInt(1) else buffer.putInt(0)
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

  implicit val dateEncoder: Encoder[js.Date] = Encoder.instance {
    (buffer, date) => buffer.putLong(date.getTime().toLong * 1000)
  }

  implicit def arrayEncoder[T](implicit
      tEncoder: Encoder[T]
  ): Encoder[Array[T]] = { (buffer, array) =>
    val length = array.length
    buffer.putInt(length)
    array.foreach(tEncoder.encode(buffer, _))
  }

  implicit def mapEncoder[V](implicit
      vEncoder: Encoder[V]
  ): Encoder[Map[String, V]] = { (buffer, map) =>
    val length = map.size
    val keyValues = map.toList.sortBy(_._1)
    buffer.putInt(length)
    keyValues.foreach { case (k, v) =>
      stringEncoder.encode(buffer, k)
      vEncoder.encode(buffer, v)
    }
  }
}
