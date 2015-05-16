import org.scalacheck.Prop._
import org.scalacheck._
import scalaz._, Scalaz._

object Compose extends Properties("Compose") {

  def compose[A, B, F[_], G[_]](f: A => B, g: F ~> G)(implicit fa: Arbitrary[F[A]], ff: Functor[F], gf: Functor[G]): Prop = {
    forAll { a: F[A] =>
      g(a.map(f)) ?=
        g(a).map(f)
    }
  }

  property("compose") =
    compose[Int, String, List, Option](
      _.toString,
      new NaturalTransformation[List, Option] {
        def apply[A](a: List[A]) = a.headOption
      }
    )
}
