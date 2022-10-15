package io.borsh4s.instances

import io.borsh4s.BinarySize

object BinarySizes:
  given BinarySize[Byte] = _ => 1

  given BinarySize[Boolean] = _ => 1

  given BinarySize[Short] = _ => 2

  given BinarySize[Int] = _ => 4

  given BinarySize[Long] = _ => 8

  given BinarySize[Float] = _ => 4

  given BinarySize[Double] = _ => 8

  given BinarySize[String] = _.length + 4

  given [T](using tSize: BinarySize[T]): BinarySize[Option[T]] =
    _.map(tSize.calculate).getOrElse(0) + 1

  given [T](using tSize: BinarySize[T]): BinarySize[Array[T]] =
    _.map(tSize.calculate).sum + 4

  given [T](using tSize: BinarySize[T]): BinarySize[Set[T]] =
    _.foldLeft(0)((acc, t) => acc + tSize.calculate(t)) + 4

  given [K, V](using
      kSize: BinarySize[K],
      vSize: BinarySize[V]
  ): BinarySize[Map[K, V]] =
    _.map { case (k, v) =>
      kSize.calculate(k) + vSize.calculate(v)
    }.sum + 4
