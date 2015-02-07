import org.scalacheck._
import org.scalacheck.Prop.forAll

object ImplicitNaming extends Properties("ImplicitNaming") {

  case class User(name: String)

  object User {
    implicit def UserArbitrary: Arbitrary[User] =
      Arbitrary(Arbitrary.arbitrary[String].map(User.apply))
  }

  case class Foo(bar: String)

  implicit def UserArbitrary: Arbitrary[Foo] =
    Arbitrary(Arbitrary.arbitrary[String].map(Foo))

  property("compiler error") = forAll { a: User =>
    true
  }
}
