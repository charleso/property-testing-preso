import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object HelloWorld extends Properties("HelloWorld") {

  property("list reverse") = forAll { a: String =>
    a.reverse.reverse == a
  }

  property("list reverse 2") = forAll { (a: String, b: String) =>
    (b + a).reverse == (a.reverse + b.reverse)
  }
}
