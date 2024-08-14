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

  sealed trait Failure(message: String) extends Exception:
    def bytes: ByteBuffer
    override def getMessage: String =
      s"$message - At position ${bytes.position()}."

  object Failure:
    final case class BufferException(
        cause: Throwable,
        override val bytes: ByteBuffer
    ) extends Failure(cause.getMessage)

    final case class InvalidBooleanValue(
        value: Byte,
        override val bytes: ByteBuffer
    ) extends Failure(s"Invalid boolean value $value")

    final case class InvalidLength(value: Int, override val bytes: ByteBuffer)
        extends Failure(s"Invalid length $value")

    final case class InvalidOptionValue(
        value: Byte,
        override val bytes: ByteBuffer
    ) extends Failure(s"Invalid option value $value")

    final case class InvalidUnionValue(
        index: Int,
        name: String,
        override val bytes: ByteBuffer
    ) extends Failure(s"Invalid union value $index for union $name")

    final case class InvalidListElement(index: Int, cause: Failure)
        extends Failure(
          s"Invalid list element at index $index. Cause: ${cause.getMessage}"
        ):
      override val bytes: ByteBuffer = cause.bytes
