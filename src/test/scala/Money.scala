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

object Money extends Properties("Money") {

  property("state") =
    MyState.property(threadCount = 2)

  object MyState extends Commands {
    case class State(a: Int, b: Int)
    case class Sut(var a: Int, var b: Int)

    def canCreateNewSut(s: State, init: Traversable[State], running: Traversable[Sut]): Boolean =
      true

    def destroySut(sut: Sut): Unit =
      ()

    def genCommand(state: State): Gen[Command] =
      Gen.choose(0, 5).map(Deposit(_))

    def genInitialState: Gen[State] =
      Gen.const(State(0,0))
    /*
      for {
        a <- Gen.choose(100, 200)
        b <- Gen.choose(100, 200)
      } yield State(a, b)
      */

    def initialPreCondition(state: State): Boolean =
      true

    def newSut(state: State): Sut =
      Sut(state.a, state.b)

    case class Deposit(i: Int) extends Command {

      type Result = Int

      def nextState(state: State): State =
        State(state.a - i, state.b + i)

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(state.a + state.b)

      def preCondition(state: State): Boolean =
        true

      def run(sut: Sut): Result = {
        sut.a -= i
        Thread.sleep(1)
        sut.b += i
        sut.a + sut.b
      }
    }
  }
}
