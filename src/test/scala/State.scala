import org.joda.time.{DateTimeZone, DateTime}
import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.scalacheck.commands._

import scala.io.Codec
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Success, Try}

object State extends Properties("State") {

  property("state") =
    MyState.property(threadCount = 2)

  object MyState extends Commands {
    type State = Map[String, String]
    type Sut = mutable.Map[String, String]

    def canCreateNewSut(s: State, init: Traversable[State], running: Traversable[Sut]): Boolean =
      true

    def destroySut(sut: Sut): Unit =
      ()

    def genCommand(state: State): Gen[Command] =
      Gen.oneOf(
          for {
            k <- arbitrary[String]
            v <- arbitrary[String]
          } yield (Insert(k, v))
        , arbitrary[String].map(Get(_))
        )

    def genInitialState: Gen[State] =
      Gen.const(Map())

    def initialPreCondition(state: State): Boolean =
      true

    def newSut(state: State): Sut =
      mutable.Map() ++ state

    case class Get(key: String) extends Command {

      type Result = Option[String]

      def nextState(state: State): State =
        state

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(state.get(key))

      def preCondition(state: State): Boolean =
        true

      def run(sut: Sut): Result =
        sut.get(key)
    }

    case class Insert(k: String, v: String) extends Command {

      type Result = Boolean

      def nextState(state: State): State =
        state + (k -> v)

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(true)

      def preCondition(state: State): Boolean =
        true

      def run(sut: Sut): Result = {
        sut += (k -> v)
        true
      }
    }
  }
}
