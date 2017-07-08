import org.joda.time.{DateTimeZone, DateTime}
import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Prop._

object Reverse extends Properties("Reverse") {

  property("state") =
    forAll(arbitrary[List[Int]]) { ls =>
      println(ls)
      reverse(reverse(ls)) ?= ls
    }

  def reverse[A : Ordering](ls: List[A]): List[A] =
    ls.sorted
}
