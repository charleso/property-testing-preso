import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.scalacheck.commands._

import scala.collection.mutable
import scala.util.{Success, Try}

object State extends Properties("State") {

  property("state") =
    MyState.property(threadCount = 1)

  object MyState extends Commands {
    type Pid = Int
    type Name = String
    case class State(
        pids : List[Pid]
      , regs : List[(Name, Pid)]
      )
    class Sut(
        val pids : mutable.MutableList[Pid]
      , val regs : mutable.Map[Name, Pid]
      )

    def canCreateNewSut(s: State, init: Traversable[State], running: Traversable[Sut]): Boolean =
      true

    def destroySut(sut: Sut): Unit =
      ()

    def genName: Gen[Name] =
      Gen.oneOf("a", "b", "c", "d")

    def genCommand(state: State): Gen[Command] = {
      Gen.oneOf(
          arbitrary[Pid].map(Spawn(_))
        , genName.map(Unregister)
        , (if (state.pids.isEmpty) Nil else List(for {
            n <- genName
            p <- Gen.oneOf(state.pids)
          } yield Register(n, p))
        ) : _*
        )
    }

    def genInitialState: Gen[State] =
      Gen.const(State(Nil, Nil))

    def initialPreCondition(state: State): Boolean =
      true

    def newSut(state: State): Sut =
      new Sut(mutable.MutableList(), mutable.Map())

    case class Spawn(pid: Pid) extends Command {

      type Result = Unit

      def nextState(state: State): State =
        state.copy(pids = state.pids ++ List(pid))

      def postCondition(state: State, result: Try[Result]): Prop =
        true

      def preCondition(state: State): Boolean =
        true

      def run(sut: Sut): Result = {
        sut.pids += pid
        ()
      }
    }

    case class Register(name: Name, pid: Pid) extends Command {

      type Result = Unit

      def nextState(state: State): State =
        state.copy(regs = state.regs ++ List(name -> pid))

      def postCondition(state: State, result: Try[Result]): Prop =
        true

      def preCondition(state: State): Boolean =
        ! state.regs.exists(x => x._1 == name || x._2 == pid)

      def run(sut: Sut): Result = {
        sut.regs += (name -> pid)
        ()
      }
    }

    case class Unregister(name: Name) extends Command {

      type Result = Unit

      def nextState(state: State): State =
        state.copy (regs = state.regs.filter(x => x._1 != name))

      def postCondition(state: State, result: Try[Result]): Prop =
        true

      def preCondition(state: State): Boolean =
        state.regs.exists(x => x._1 == name)

      def run(sut: Sut): Result = {
        sut.regs.get(name) match {
          case None =>
            sys.error("Not registered")
          case Some(x) =>
            sut.regs -= name
            ()
        }
      }
    }
  }
}
