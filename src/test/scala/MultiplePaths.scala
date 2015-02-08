import org.scalacheck._
import org.scalacheck.Prop._

object MultiplePaths extends Properties("MultiplePaths") {

  property("partition") = forAll { l: List[Boolean] =>
    l.partition(identity) =? (l.filter(identity) -> l.filterNot(identity))
  }

  property("take and drop") = forAll { (l: List[Int], i: Int) =>
    l.take(i) ++ l.drop(i) =? l
  }

  property("size and length") = forAll { l: List[Boolean] =>
    l.size =? l.length
  }

  property("splitAt") = forAll { (l: List[Int], i: Int) =>
    l.take(i) -> l.drop(i) =? l.splitAt(i)
  }

  property("fold") = forAll { (o: Option[Unit], i1: Int, i2: Int) =>
    o.fold(i1)(_ => i2) =? o.map(_ => i2).getOrElse(i1)
  }
}
