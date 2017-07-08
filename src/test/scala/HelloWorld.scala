import org.scalacheck._
import org.scalacheck.Prop._

object HelloWorld extends Properties("HelloWorld") {

  property("list reverse") = forAll { a: String =>
    a.reverse.reverse =? a
  }

  property("list reverse 2") = forAll { (a: String, b: String) =>
    (b + a).reverse =? (a.reverse + b.reverse)
  }

  property("prime") = forAll(Gen.choose(2, 100)) { (i: Int) =>
    println(i + (if (isPrime(i)) "*" else ""))
    !isPrime(i)
  }

  def isPrime(i :Int) : Boolean = {
     if (i <= 1)
       false
     else if (i == 2)
       true
     else
       !(2 to (i-1)).exists(x => i % x == 0)
   }
}
