import org.joda.time.{DateTimeZone, DateTime}
import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.scalacheck.commands._

import scala.io.Codec
import java.io.File
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Success, Try}

object StateFile extends Properties("StateFile") {

  property("statefile") =
    MyState.property(threadCount = 1)

  object MyState extends Commands {
    type State = Map[String, String]
    type Sut = File

    def canCreateNewSut(s: State, init: Traversable[State], running: Traversable[Sut]): Boolean =
      true

    def destroySut(sut: Sut): Unit = {
      new java.io.File(sut, "a").delete
      new java.io.File(sut, "b").delete
      new java.io.File(sut, "c").delete
      sut.delete
      ()
    }

    def genCommand(state: State): Gen[Command] =
      Gen.oneOf(
          for {
            k <- Gen.oneOf("a", "b", "c")
            v <- arbitrary[String]
          } yield (Insert(k, v))
        , Gen.oneOf("a", "b", "c").map(Get(_))
        )

    def genInitialState: Gen[State] =
      Gen.const(Map())

    def initialPreCondition(state: State): Boolean =
      true

    def newSut(state: State): Sut = {
      val f = new java.io.File("deletem", System.currentTimeMillis.toString)
      f.mkdirs()
      f
    }

    case class Get(key: String) extends Command {

      type Result = Option[String]

      def nextState(state: State): State =
        state

      def postCondition(state: State, result: Try[Result]): Prop =
        result =? Success(state.get(key))

      def preCondition(state: State): Boolean =
        true

      def run(sut: Sut): Result = {
        val f = new java.io.File(sut, key)
        if (f.exists)
          Some(scala.io.Source.fromFile(f).mkString)
        else
          None
      }
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
        val fos = new java.io.FileOutputStream(new java.io.File(sut, k))
        fos.write(v.getBytes)
        true
      }
    }
  }
}
