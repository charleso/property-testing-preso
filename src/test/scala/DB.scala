import com.ambiata.disorder.DistinctPair
import org.scalacheck._
import org.scalacheck.Prop._

object DBTest extends Properties("DB") {

  property("insert empty") = forAll { e1: Entity =>
    run(for {
      id <- EntityDB.insert(e1)
      e2 <- EntityDB.get(id)
    } yield e2 =? Some(e1))
  }

  property("insert non-empty") = forAll { (e1: Entity, e2: Entity) =>
    run(for {
      id1 <- EntityDB.insert(e1)
      id2 <- EntityDB.insert(e2)
      e3 <- EntityDB.get(id2)
    } yield e3 =? Some(e2))
  }

  property("empty") = forAll { (id: Int) =>
    run(for {
      e <- EntityDB.get(id)
    } yield e =? None)
  }

  property("insert non-empty unique") = forAll { (e1: Entity, e2a: Entity, names: DistinctPair[Int]) =>
    val e2b = e2a.copy(name = names.second.toString)
    run(for {
      i1 <- EntityDB.insertUnique(e1.copy(name = names.first.toString))
      i2 <- EntityDB.insertUnique(e2b)
      e3 <- i2.map(EntityDB.get).getOrElse(DB.pure(Option.empty[Entity]))
    } yield e3 =? Some(e2b))
  }

  property("insert non-empty duplicate") = forAll { (e1: Entity, e2: Entity, name: String) =>
    run(for {
      i1 <- EntityDB.insertUnique(e1.copy(name = name))
      i2 <- EntityDB.insertUnique(e2.copy(name = name))
    } yield (i1.isDefined,i2.isEmpty) =? (true -> true))
  }

  def run(db: DB[Prop]): Prop =
    db.run(Vector.empty)._2
}

case class Entity(name: String)

object Entity {

  implicit def EntityArbitrary: Arbitrary[Entity] =
    Arbitrary(for {
      n <- Arbitrary.arbitrary[String]
    } yield Entity(n))
}

/** Hard-coded State monad */
case class DB[A](run: Vector[Entity] => (Vector[Entity], A)) {

  def map[B](f: A => B): DB[B] =
    DB(v => v -> f(run(v)._2))

  def flatMap[B](f: A => DB[B]): DB[B] =
    DB { v => val v2 = run(v); f(v2._2).run(v2._1)}
}

object DB {

  def pure[A](a: A): DB[A]
  = DB(_ -> a)
}

object EntityDB {

  def insert(e: Entity): DB[Int] =
    DB(v => (v :+ e) -> v.size)

  def insertUnique(e: Entity): DB[Option[Int]] =
    DB(v => if (v.exists(_.name == e.name)) v -> None else (v :+ e) -> Some(v.size))

  def get(id: Int): DB[Option[Entity]] =
    DB(v => v -> (if (id >=0 && id < v.size) Some(v(id)) else None))
}
