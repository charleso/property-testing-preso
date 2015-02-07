import org.scalacheck.Prop._
import org.scalacheck.Properties

object Lists extends Properties("Lists") {

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

  property("partition") = forAll { l: List[Boolean] =>
    l.partition(identity) =? (l.filter(identity) -> l.filterNot(identity))
  }

  property("fold") = forAll { (o: Option[Unit], i1: Int, i2: Int) =>
    o.fold(i1)(_ => i2) =? o.map(_ => i2).getOrElse(i1)
  }
}
