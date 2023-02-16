package io.borsh4s

import io.borsh4s.Naturals.Nat

sealed trait BinarySize[T]:
  def calculate(t: T): Nat

object BinarySize:
  final case class Constant[T](value: Nat) extends BinarySize[T]:
    override def calculate(t: T): Nat = value

  object Constant:
    def One[T]: BinarySize.Constant[T] = Constant(Nat.One)
    def Two[T]: BinarySize.Constant[T] = Constant(Nat.Two)
    def Four[T]: BinarySize.Constant[T] = Constant(Nat.Four)
    def Eight[T]: BinarySize.Constant[T] = Constant(Nat.Eight)

  trait Function[T] extends BinarySize[T]:
    override def calculate(t: T): Nat

  object Function:
    def apply[T](calculate: T => Nat): Function[T] =
      calculate(_)
