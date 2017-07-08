import org.scalacheck._
import org.scalacheck.Prop._

object HelloWorld extends Properties("HelloWorld") {

  property("list reverse") = forAll { a: String =>
    a.reverse.reverse =? a
  }

  property("list reverse 2") = forAll { (a: String, b: String) =>
    (b + a).reverse =? (a.reverse + b.reverse)
  }

  property("substring") = forAll { (a: String, i: Int) =>
    (i >= 0 && i < a.length) ==> {
      a.substring(i)
      true
    }
  }
}
