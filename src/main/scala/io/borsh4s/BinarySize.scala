package io.borsh4s

import magnolia1.*

trait BinarySize[T] {
  def calculate(t: T): Int
}

object BinarySize {
  type Typeclass[T] = BinarySize[T]

  def join[T](ctx: CaseClass[BinarySize, T]): BinarySize[T] =
    (t: T) =>
      ctx.parameters.map(p => p.typeclass.calculate(p.dereference(t))).sum

  implicit def gen[T]: BinarySize[T] = macro Magnolia.gen[T]
}
