package io.borsh4s.instances

import io.borsh4s.*
import io.borsh4s.Decoder.*
import io.borsh4s.Naturals.Nat
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

object Decoders:
  given Decoder[Byte] = _.read(_.get())

  given Decoder[Short] = _.read(_.getShort())

  given intDecoder: Decoder[Int] = _.read(_.getInt())

  private val natDecoder: Decoder[Nat] =
    intDecoder.flatMap: int =>
      bytes => Nat(int).toRight(Failure.InvalidLength(int, bytes))

  given Decoder[Long] = _.read(_.getLong())

  given Decoder[Float] = _.read(_.getFloat())

  given Decoder[Double] = _.read(_.getDouble())

  given Decoder[Boolean] =
    bytes =>
      bytes
        .read(_.get())
        .flatMap:
          case 0x0   => Right(false)
          case 0x1   => Right(true)
          case other => Left(Failure.InvalidBooleanValue(other, bytes))

  given Decoder[String] =
    bytes =>
      for
        length <- natDecoder.decode(bytes)
        byteArray = new Array[Byte](length.toInt)
        _ <- bytes.read(_.get(byteArray))
      yield new String(byteArray, StandardCharsets.UTF_8)

  given [T](using tDecoder: Decoder[T]): Decoder[Option[T]] =
    bytes =>
      bytes
        .read(_.get())
        .flatMap:
          case 0x0   => Right(None)
          case 0x1   => tDecoder.decode(bytes).map(Option.apply)
          case other => Left(Failure.InvalidOptionValue(other, bytes))

  given [T: ClassTag](using tDecoder: Decoder[T]): Decoder[Array[T]] =
    decodeList(_).map(_.toArray)

  given [T](using tDecoder: Decoder[T]): Decoder[Set[T]] =
    decodeList(_).map(_.toSet)

  @SuppressWarnings(Array("org.wartremover.contrib.warts.ExposedTuples"))
  given [K, V](using
      kDecoder: Decoder[K],
      vDecoder: Decoder[V]
  ): Decoder[(K, V)] =
    bytes =>
      for
        key <- kDecoder.decode(bytes)
        value <- vDecoder.decode(bytes)
      yield (key, value)

  given [K, V](using Decoder[K], Decoder[V]): Decoder[Map[K, V]] =
    decodeList[(K, V)](_).map(_.toMap)

  private def decodeList[T](
      bytes: ByteBuffer
  )(using Decoder[T]): Result[List[T]] =
    for
      nat <- natDecoder.decode(bytes)
      list <- repeatDecode(bytes, nat)
    yield list

  @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
  private def repeatDecode[T](bytes: ByteBuffer, length: Nat)(using
      decoder: Decoder[T]
  ): Result[List[T]] = {
    val buffer = ListBuffer.empty[T]

    @tailrec def loop(current: Nat): Result[List[T]] =
      current.decrement match {
        case None              => Right(buffer.toList)
        case Some(predecessor) =>
          decoder.decode(bytes) match {
            case Right(value) =>
              buffer.append(value)
              loop(predecessor)
            case Left(failure) =>
              Left(Failure.InvalidListElement(length - current, failure))
          }
      }

    loop(length)
  }
