# Borsh4s
Scala implementation of [Borsh](https://borsh.io/) serialization format.

## Motivation
There are Java and Javascript implementations for Borsh, but both are very inconvenient to use from Scala on JVM and ScalaJS. The Java implementation uses POJOs and reflection (discouraged in Scala ecosystem) which makes interoperability hard. The JS interoperability is even harder because it uses, for example, the class constructor function as the key of a map where schemas must be declared. This adds a lot of boilerplate required for ScalaJS developers to make it work.

This project aims to be an idiomatic Scala implementation of the Borsh binary serialization format. Cross compiled for Scala on JVM and ScalaJS. Based on type classes and automatic type class derivation, without reflection or any other "unsafe" runtime tools.

## Quick start

### Requirements

- [sbt](https://www.scala-sbt.org/)
- JDK to build for the JVM
- Node.js to build for ScalaJS

The exact versions being used for these dependencies are defined in the
[`.tool-versions`](.tool-versions) file.

### Add the dependency

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

Base types:
Borsh | Scala
:---:|:---:
`i8` | `Byte`
`i16` | `Short`
`i32` | `Int`
`i64` | `Long`
`f32` | `Float`
`f64` | `Double`
`()` | `Unit`
`bool` | `Boolean`
`String` | `String`


For all supported type `T`:
Borsh | Scala
:---:|:---:
`Option<T>` | `Option[T]`
`Vec<T>` | `Array[T]`
`HashMap<String, T>` | `Map[String, T]`

For all supported types `T0` ... `TN`
Borsh | Scala
:---:|:---:
`struct Name { field0: T0, ..., fieldN: TN }` | `case class Name(field0: T0, ..., fieldN: TN)`

## How does it work?

Borsh4s is implemented using the [Type class](https://en.wikipedia.org/wiki/Type_class) pattern and exposes two interfaces:

```scala
def encode[T: Encoder: BinarySize](t: T): Array[Byte]

def decode[T: Decoder](bytes: Array[Byte]): T
```

To encode an instance of `T`, instances of the following type classes must be available in the implicit scope:
- [`io.borsh4s.Encoder[T]`](src/main/scala/io/borsh4s/Encoder.scala) - Implements the encoding logic.
- [`io.borsh4s.BinarySize[T]`](src/main/scala/io/borsh4s/BinarySize.scala) - Calculates the total size of the byte array that is going to be created to write the encoded object.

To decode an instance of `T`, only an instance of [`io.borsh4s.Decoder[T]`](src/main/scala/io/borsh4s/Decoder.scala) is needed, which implements the decoding logic.

Instances of the supported base and collection types as well as automatic derivation for `case class`es are provided out-of-the-box. To make all of them available in the implicit scope, use the following import:

```scala
import io.borsh4s.Implicits._
```

### Providing implementations for unsupported types

Custom implementations are not recommended because they may deviate from Borsh
specification. However, this project doesn't support all possible Borsh types,
so some custom implementations might be needed.

In order to add support for a type `T`, make implementations for the
`io.borsh4s.Encoder[T]`, `io.borsh4s.BinarySize[T]`, and `io.borsh4s.Decoder[T]` type classes and make sure
they are in the implicit scope of any `io.borsh4s.encode` and
`io.borsh4s.decode` calls.

Notice that these type classes use `java.nio.ByteBuffer` which is mutable. Make
sure that any custom implementation moves the position of this buffer exactly
as the size of the type being read/written requires, otherwise it will break the
whole decoding/encoding process. Find examples in `io.borsh4s.instances.Encoders`
and `io.borsh4s.instances.Decoders`.

## Future work

- `Enums`
- `HashSet`s
- `HashMap`s with arbitrary key type
- Unsigned integers
- Schemas
- Cross compilation for Scala 3
