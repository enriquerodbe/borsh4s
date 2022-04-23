package io.borsh4s.instances

import io.borsh4s.BinarySize

trait BinarySizes {
  implicit val byteSize: BinarySize[Byte] = _ => 1

  implicit val booleanSize: BinarySize[Boolean] = _ => 1

  implicit val shortSize: BinarySize[Short] = _ => 2

  implicit val intSize: BinarySize[Int] = _ => 4

  implicit val longSize: BinarySize[Long] = _ => 8

  implicit val floatSize: BinarySize[Float] = _ => 4

  implicit val doubleSize: BinarySize[Double] = _ => 8

  implicit val stringSize: BinarySize[String] = _.length + 4

  implicit def optionSize[T](implicit
      tSize: BinarySize[T]
  ): BinarySize[Option[T]] =
    _.map(tSize.calculate).getOrElse(0) + 1

  implicit def arraySize[T](implicit
      tSize: BinarySize[T]
  ): BinarySize[Array[T]] =
    _.map(tSize.calculate).sum + 4

  implicit def setSize[T](implicit tSize: BinarySize[T]): BinarySize[Set[T]] =
    _.foldLeft(0)((acc, t) => acc + tSize.calculate(t)) + 4

  implicit def mapSize[V](implicit
      vSize: BinarySize[V]
  ): BinarySize[Map[String, V]] =
    _.map { case (k, v) =>
      stringSize.calculate(k) + vSize.calculate(v)
    }.sum + 4
}
