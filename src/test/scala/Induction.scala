import org.scalacheck._
import org.scalacheck.Prop._

object Induction extends Properties("ThereBackAgain") {

  property("size base") =
    Nil.size =? 0

  property("size normal") = forAll { (i: Int, l: List[Int]) =>
    (i :: l).size =? (l.size + 1)
  }

  property("headOption base") =
    Nil.headOption =? None

  property("headOption normal") = forAll { (i: Int, l: List[Int]) =>
    (i :: l).headOption =? Some(i)
  }
}
