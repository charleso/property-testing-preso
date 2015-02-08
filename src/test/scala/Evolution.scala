import org.scalacheck._
import org.scalacheck.Prop._

object Evolution extends Properties("Lists") {

  property("step 1") = forAll { l: List[Int] =>
    l.headOption =? (if (l.isEmpty) None else Some(l.head))
  }

  property("step 2") = forAll { l: List[Int] => !l.isEmpty ==> {
    l.headOption =? Some(l.head)
  }}

  property("step 3 - empty") =
    Nil.headOption =? None

  property("step 3 - non-empty") = forAll { (h: Int, t: List[Int]) =>
    (h :: t).headOption =? Some(h)
  }

  /*
  property("discarded") = forAll { l: List[Int] => l.isEmpty ==> {
    l.isEmpty
  }}
  */

  import scalaz._
  import scalaz.scalacheck.ScalazArbitrary._

  property("step 4 - NEL") = forAll { l: NonEmptyList[Int] =>
    l.list.headOption =? Some(l.head)
  }
}
