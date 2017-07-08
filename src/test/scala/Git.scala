import org.joda.time.{DateTimeZone, DateTime}
import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.scalacheck.commands._
import sys.process._

import java.io.File
import scala.io.Codec
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Success, Try}

object Git extends Properties("Git") {

  property("git") =
    MyState.property(threadCount = 1)

  object MyState extends Commands {
    case class State(
      working: Map[String, String]
    , index: Map[String, String]
    )
    type Sut = File

    def canCreateNewSut(s: State, init: Traversable[State], running: Traversable[Sut]): Boolean =
      true

    def destroySut(sut: Sut): Unit = {
      Process(List("rm", "-rf", sut.toString)).!
      ()
    }

    def genAscii: Gen[String] =
      Gen.nonEmptyListOf(Gen.choose('a', 'z')).map(_.mkString)

    def genCommand(state: State): Gen[Command] = {
      val l = List(
          Some(for {
            i <- Gen.choose(1, 5)
            k <- Gen.listOfN(i, genAscii).map(_.mkString("/")).filter(x => !state.working.contains(x))
            v <- arbitrary[String]
          } yield Write(k, v))
        , if(state.index.isEmpty) None else Some(genAscii.map(Commit))
        , if(state.working.isEmpty) None else Some(Gen.oneOf[String](state.working.keys.toList).map(Add))
        ).flatten
      l match {
        case List(l) =>
          l
        case List(l1, l2) =>
          Gen.oneOf(l1, l2)
        case l1 :: l2 :: ls =>
          Gen.oneOf(l1, l2, ls: _*)
      }
    }

    def genInitialState: Gen[State] =
      Gen.const(State(Map(), Map()))

    def initialPreCondition(state: State): Boolean =
      true

    def newSut(state: State): Sut = {
      val sut = new File("deleteme", System.currentTimeMillis.toString)
      sut.mkdirs()
      Seq("git", "init", sut.toString).!!
      sut
    }

    case class Write(k: String, v: String) extends Command {

      type Result = Unit

      def nextState(state: State): State =
        state.copy(working = (state.working + (k -> v)))

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(())

      def preCondition(state: State): Boolean =
        true

      def run(sut: Sut): Result = {
        val f = new File(sut, k)
        f.getParentFile.mkdirs()
        val p = new java.io.FileWriter(f)
        p.write(v)
        p.close()
      }
    }

    case class Add(k: String) extends Command {

      type Result = Int

      def nextState(state: State): State =
        state.copy(index = state.index + (k -> state.working(k)))

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(0)

      def preCondition(state: State): Boolean =
        state.working.contains(k)

      def run(sut: Sut): Result = {
        Process(List("git", "add", k.toString), Some(sut)).!
      }
    }

    case class Commit(m: String) extends Command {

      type Result = Int

      def nextState(state: State): State = {
        State(Map(), Map())
      }

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(0)

      def preCondition(state: State): Boolean =
        !state.index.isEmpty

      def run(sut: Sut): Result = {
        Process(List("git", "commit", "-m", m), Some(sut)).!
      }
    }
  }
}
