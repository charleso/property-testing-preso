import com.ambiata.disorder.PositiveIntSmall
import org.scalacheck.Prop._
import org.scalacheck._

object Idempotence extends Properties("Idempotence") {

  def idempotentSimple[A: Arbitrary](f: A => A) =
    forAll { a: A =>
      f(f(a)) =? f(a)
    }

  def idempotent[A: Arbitrary](f: A => A) =
    forAll { (a: A, i: PositiveIntSmall) =>
      (0 until i.value).foldLeft(a)((a, _) => f(a)) =? f(a)
    }

  property("list sort") =
    idempotent[List[Int]](_.sorted)

  property("list distinct") =
    idempotent[List[Int]](_.distinct)

  // Definitely not idempotent! :)
  property("list take") =
    idempotent[List[Int]](_.drop(1))
}
