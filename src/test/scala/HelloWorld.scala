import org.scalacheck._
import org.scalacheck.Prop._

object HelloWorld extends Properties("HelloWorld") {

  property("list reverse") = forAll { a: String =>
    a.reverse.reverse =? a
  }

  property("list reverse 2") = forAll { (a: String, b: String) =>
    Gen.nonEmptyListOf(Gen.alphaNumChar)
    (b + a).reverse =? (a.reverse + b.reverse)
  }
}
