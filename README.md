# borsh4s
Scala implementation of [Borsh](https://borsh.io/) serialization format.

## Motivation
There are Java and Javascript implementations for Borsh, but both are very inconvenient to use from Scala on JVM and ScalaJS. The Java implementation uses POJOs and reflection (discouraged in Scala ecosystem) which makes interoperability hard. The JS interoperability is even harder because it uses, for example, the class constructor function as the key of a map where schemas must be declared. This adds a lot of boilerplate required for ScalaJS developers to make it work.

This project aims to be an idiomatic Scala implementation of the Borsh binary serialization format. Cross compiled for Scala on JVM and ScalaJS. Based on typeclasses and automatic typeclass derivation, without reflection or any other "unsafe" runtime tools.

## Getting started

### Requirements

- [sbt](https://www.scala-sbt.org/)
- JDK to build for the JVM
- Node.js to build for ScalaJS

The exact versions being used for these dependencies are defined in the
[`.tool-versions`](.tool-versions) file.

### Adding the dependency

```scala
libraryDependencies += "io.github.enriquerodbe" %% "borsh4s" % "<version>"
```

Find the latest version in [Releases](https://github.com/enriquerodbe/borsh4s/releases), and remember to use
`%%%` for ScalaJS.

### Code example

```scala
import io.borsh4s
import io.borsh4s.Implicits._

case class MyTestClass(field1: Int, field2: String, nested: NestedClass)
case class NestedClass(field1: Boolean, field2: Map[String, Float])

val instance = MyTestClass(2, "Hello", NestedClass(true, Map("World" -> 1.5f)))
    
val encoded = borsh4s.encode(instance)
val decoded = borsh4s.decode[MyTestClass](encoded)
    
assert(instance == decoded)
```

## Supported types

The supported primitive types are: `Byte`, `Boolean`, `Short`, `Int`, `Long`,
`Float`, `Double`, `String`.

For all supported type `T`, `Array[T]`, `Option[T]`, and `Map[String, T]` are
supported.

Also, automatic derivation is provided for any `case class` composed of
supported types by importing `io.borsh4s.Implicits._`.

### Providing implementations for unsupported types

Custom implementations are not recommended because they may deviate from Borsh
specification. However, this project doesn't support all possible Scala types,
so some custom implementations might be needed.

In order to add support for a type `T`, make implementations for the
`io.borsh4s.Encoder[T]` and `io.borsh4s.Decoder[T]` typeclasses and make sure
they are in the implicit scope of any `io.borsh4s.encode` and
`io.borsh4s.decode` calls.

Notice that these typeclasses use `java.nio.ByteBuffer` which is mutable. Make
sure that any custom implementation moves the position of this buffer exactly
as the size of the type being read/written requires, otherwise it will break the
whole de/serialization process. Find examples in `io.borsh4s.instances.Encoders`
and `io.borsh4s.instances.Decoders`.

## Future work

- Support for `sealed traits`
- Cross compilation for Scala 3
