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
    , commits: List[Map[String, String]]
    ) {

      def unstaged: Set[String] =
        working.keySet -- index.keySet

      def staged: Map[String, String] =
        commits match {
          case Nil =>
            index
          case h :: _ =>
            index.flatMap({
              case (k, v) =>
                h.get(k) match {
                  case None =>
                    Map(k -> v)
                  case Some(v2) =>
                    if (v == v2)
                      Map[String, String]()
                    else
                      Map(k -> v)
                }
            })
        }
    }
    type Sut = File

    def canCreateNewSut(s: State, init: Traversable[State], running: Traversable[Sut]): Boolean =
      true

    def destroySut(sut: Sut): Unit = {
      Process(List("rm", "-rf", sut.toString)).!
      ()
    }

    def genAscii: Gen[String] =
      Gen.nonEmptyListOf(Gen.choose('a', 'z')).map(_.mkString)

    def isSubset(f: File, f2: File): Boolean = {
      Stream.iterate(f)(_.getParentFile).takeWhile(_ != null).contains(f2)
    }

    def genCommand(state: State): Gen[Command] = {
      val l = List(
          Some(for {
            i <- Gen.choose(1, 5)
            k <- Gen.listOfN(i, genAscii).map(_.mkString("/"))
              // .filter(x => !state.working.contains(x))
              .filter(x => state.working.forall(a =>
                !isSubset(new File(a._1), new File(x)) && !isSubset(new File(x), new File(a._1))
              ))
            v <- arbitrary[String]
          } yield Write(k, v))
        , if(state.staged.isEmpty) None else Some(genAscii.map(Commit))
        , if(state.unstaged.isEmpty) None else Some(Gen.oneOf(state.unstaged.toList).map(Add))
        , if(state.staged.isEmpty) None else Some(Gen.oneOf(state.index.keys.toList).map(Reset))
        , if(state.working.isEmpty) None else Some(Gen.oneOf(state.working.keys.toList).map(Delete))
        // , if(state.commits.length < 2) None else Some(Gen.choose(1, state.commits.length - 1).map(ResetHard))
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
      Gen.const(State(Map(), Map(), Nil))

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

    case class Delete(k: String) extends Command {

      type Result = Boolean

      def nextState(state: State): State =
        state.copy(working = (state.working - k))

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(true)

      def preCondition(state: State): Boolean =
        state.working.contains(k)

      def run(sut: Sut): Result = {
        val f = new File(sut, k)
        f.delete()
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
        state.copy(commits = state.index :: state.commits)
      }

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(0)

      def preCondition(state: State): Boolean =
        !state.index.isEmpty

      def run(sut: Sut): Result = {
        Process(List("git", "commit", "-m", m), Some(sut)).!
      }
    }

    case class Reset(k: String) extends Command {

      type Result = Int

      def nextState(state: State): State = {
        state.copy(
          index = state.index + (k -> state.staged(k))
        )
      }

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(0)

      def preCondition(state: State): Boolean =
        state.index.contains(k)

      def run(sut: Sut): Result = {
        Process(List("git", "reset", "--", k), Some(sut)).!
      }
    }

    case class ResetHard(k: Int) extends Command {

      type Result = Int

      def nextState(state: State): State = {
        State(
          working = state.working ++ state.commits(k)
        , index = Map()
        , commits = state.commits.drop(k)
        )
      }

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(0)

      def preCondition(state: State): Boolean =
        0 < k && k < state.commits.length - 1

      def run(sut: Sut): Result = {
        Process(List("git", "reset", "--hard", "HEAD~" + k), Some(sut)).!
      }
    }
  }
}
