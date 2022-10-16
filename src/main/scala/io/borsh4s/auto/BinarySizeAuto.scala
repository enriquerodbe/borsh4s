package io.borsh4s.auto

import io.borsh4s.BinarySize
import magnolia1.*

object BinarySizeAuto extends AutoDerivation[BinarySize]:
  override def join[T](ctx: CaseClass[BinarySize, T]): BinarySize[T] =
    t => ctx.params.map(p => p.typeclass.calculate(p.deref(t))).sum

  override def split[T](ctx: SealedTrait[BinarySize, T]): BinarySize[T] =
    t => 1 + ctx.choose(t)(sub => sub.typeclass.calculate(sub.cast(t)))
