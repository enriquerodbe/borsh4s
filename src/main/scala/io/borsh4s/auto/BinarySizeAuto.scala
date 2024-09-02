package io.borsh4s.auto

import io.borsh4s.BinarySize
import io.borsh4s.BinarySize.{Constant, Function}
import io.borsh4s.Naturals.Nat
import magnolia1.*

object BinarySizeAuto extends AutoDerivation[BinarySize]:
  override def join[T](ctx: CaseClass[BinarySize, T]): BinarySize[T] =
    val (constants, functions) = ctx.params.partitionMap: param =>
      param.typeclass match
        case Constant(value) => Left(value)
        case f: Function[_]  => Right((t: T) => f.calculate(param.deref(t)))
    val constantPart = constants.sum
    if functions.isEmpty then Constant(constantPart)
    else Function { t => constantPart + functions.map(_.apply(t)).sum }

  override def split[T](ctx: SealedTrait[BinarySize, T]): BinarySize[T] =
    Function: t =>
      Nat.One + ctx.choose(t)(sub => sub.typeclass.calculate(sub.cast(t)))
