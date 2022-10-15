package io.borsh4s

trait BinarySize[T]:
  def calculate(t: T): Int
