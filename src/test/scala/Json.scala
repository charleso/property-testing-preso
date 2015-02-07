import argonaut._, Argonaut._

import org.scalacheck._
import org.scalacheck.Prop._
import scalaz._, Scalaz._

object JsonProp extends Properties("JSON") {

  case class User(name: String)

  implicit def UserArbitrary: Arbitrary[User] =
    Arbitrary(Arbitrary.arbitrary[String].map(User))

  implicit def UserJson: CodecJson[User] =
    casecodec1(User.apply, User.unapply)("name")

  case class BadUser(name: String)

  implicit def BadUserArbitrary: Arbitrary[BadUser] =
    Arbitrary(Arbitrary.arbitrary[String].map(BadUser))

  implicit def BadUserJson: CodecJson[BadUser] =
    CodecJson.derived(jencode1L(BadUser.unapply)("badname"), jdecode1L(BadUser.apply)("name"))

  implicit def UserEqual: Equal[User] =
    Equal.equalA[User]

  property("user json") =
    json[User]

  property("user json law") = forAll { a: User =>
    CodecJson.derived[User].codecLaw.encodedecode(a)
  }

  property("baduser json") =
    json[BadUser]

  def json[A: Arbitrary: DecodeJson: EncodeJson]: Prop = forAll { a: A =>
    Parse.decode[A](a.asJson.spaces2) =? a.right
  }
}
