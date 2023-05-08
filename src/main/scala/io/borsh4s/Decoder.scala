package io.borsh4s

import io.borsh4s.Decoder.Result
import java.nio.ByteBuffer
import scala.util.Try

trait Decoder[T]:
  def decode(bytes: ByteBuffer): Result[T]
  def flatMap[R](f: T => Decoder[R]): Decoder[R] =
    bytes => decode(bytes).flatMap(f(_).decode(bytes))

object Decoder:
  type Result[T] = Either[Failure, T]

  extension [T](bytes: ByteBuffer)
    def read(f: ByteBuffer => T): Result[T] =
      Try(f(bytes)).toEither.left.map(Failure.BufferException(_, bytes))

  sealed trait Failure(message: String, val bytes: ByteBuffer)
      extends Exception:
    override def getMessage: String =
      s"$message - At position ${bytes.position()}."

  object Failure:
    final case class BufferException(cause: Throwable, _bytes: ByteBuffer)
        extends Failure(cause.getMessage, _bytes)

    final case class InvalidBooleanValue(value: Byte, _bytes: ByteBuffer)
        extends Failure(s"Invalid boolean value $value", _bytes)

    final case class InvalidLength(value: Int, _bytes: ByteBuffer)
        extends Failure(s"Invalid length $value", _bytes)

    final case class InvalidOptionValue(value: Byte, _bytes: ByteBuffer)
        extends Failure(s"Invalid option value $value", _bytes)

    final case class InvalidUnionValue(
        index: Int,
        name: String,
        _bytes: ByteBuffer
    ) extends Failure(s"Invalid union value $index for union $name", _bytes)

    final case class InvalidListElement(index: Int, cause: Failure)
        extends Failure(
          s"Invalid list element at index $index. Cause: ${cause.getMessage}",
          cause.bytes
        )
