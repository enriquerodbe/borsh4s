package io.borsh4s

import scala.annotation.targetName

private object Naturals:
  opaque type Nat = Int

  object Nat:
    def apply(i: Int): Option[Nat] = Option.when(i >= 0)(i)
    val One: Nat = 1
    val Two: Nat = 2
    val Four: Nat = 4
    val Eight: Nat = 8

    extension [T](iter: Iterable[T])
      def sumBy(f: T => Nat): Nat =
        iter.foldLeft(0)((acc, t) => acc + f(t))
    extension (iter: Iterable[Nat]) def sum: Nat = iter.sumBy(identity)

  extension (n: Nat)
    def toInt: Int = n
    @targetName("subtract") def -(other: Nat): Int = n - other
    @targetName("add") def +(other: Nat): Nat = n + other
    @targetName("multiply") def *(other: Nat): Nat = n * other
    def decrement: Option[Nat] = Nat(n - 1)
