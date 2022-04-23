package io.borsh4s.instances

import io.borsh4s.Decoder
import java.nio.charset.StandardCharsets
import scala.reflect.ClassTag

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
trait Decoders {
  implicit val byteDecoder: Decoder[Byte] = _.get

  implicit val booleanDecoder: Decoder[Boolean] = _.get == 0x1

  implicit val shortDecoder: Decoder[Short] = _.getShort

  implicit val intDecoder: Decoder[Int] = _.getInt

  implicit val longDecoder: Decoder[Long] = _.getLong

  implicit val floatDecoder: Decoder[Float] = _.getFloat

  implicit val doubleDecoder: Decoder[Double] = _.getDouble

  implicit val stringDecoder: Decoder[String] = { buffer =>
    val length = buffer.getInt
    val byteArray = new Array[Byte](length)
    buffer.get(byteArray)
    new String(byteArray, StandardCharsets.UTF_8)
  }

  implicit def optionDecoder[T](implicit
      tDecoder: Decoder[T]
  ): Decoder[Option[T]] = { buffer =>
    buffer.getShort() match {
      case 0 => None
      case 1 => Some(tDecoder.decode(buffer))
    }
  }

  implicit def arrayDecoder[T: ClassTag](implicit
      tDecoder: Decoder[T]
  ): Decoder[Array[T]] = { buffer =>
    val length = buffer.getInt
    (0 until length).map(_ => tDecoder.decode(buffer)).toArray
  }

  implicit def mapDecoder[V](implicit
      vDecoder: Decoder[V]
  ): Decoder[Map[String, V]] = { buffer =>
    val length = buffer.getInt
    (0 until length).map { _ =>
      val key = stringDecoder.decode(buffer)
      val value = vDecoder.decode(buffer)
      (key, value)
    }.toMap
  }
}
