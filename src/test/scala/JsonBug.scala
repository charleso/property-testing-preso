import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._

object JsonBug extends Properties("JsonBug") {

//  include(JsonBug1)
//  include(JsonBug2)
//  include(JsonBug3)
//  include(CsvBug)
//  include(CodecBug)
  include(CsvBug2)

  object JsonBug1 extends Properties("JsonBug1") {

    import scala.util.parsing.json._

    property("bug") = forAll { s: JSONType =>
      JSON.parseRaw(s.toString()) =? Some(s)
    }

    implicit def ArbitaryJson: Arbitrary[JSONType] =
      Arbitrary(genJSON)

    def genJSON: Gen[JSONType] = for {
      j <- Gen.sized(s => Gen.resize(s / 2, genJSONSub))
      a <- Gen.oneOf(Gen.listOf(j).map(JSONArray),
        Gen.listOf(Gen.zip(arbitrary[String], j)).map(l => JSONObject(l.toMap)))

    } yield a

    def genJSONSub: Gen[Any] =
      Gen.oneOf(arbitrary[String], arbitrary[Int], arbitrary[Boolean], arbitrary[Double], genJSON)
  }

  object JsonBug2 extends Properties("JsonBug2") {

    import org.json.simple._
    import scala.collection.JavaConverters._

    property("bug") = forAll { s: JSONAware =>
      toScala(JSONValue.parse(s.toJSONString)) =? toScala(s)
    }

    implicit def ArbitaryJSONAware: Arbitrary[JSONAware] =
      Arbitrary(genJSON)

    /** To make equals possible */
    def toScala(j: Any): Any = {
      j match {
        case a: JSONArray => a.asInstanceOf[java.util.List[Any]].asScala.toList.map(toScala)
        case a: JSONObject => a.asInstanceOf[java.util.Map[String, Any]].asScala.mapValues(toScala)
        case a => a
      }
    }

    def genJSON: Gen[JSONAware] = for {
      j <- Gen.sized(s => Gen.resize(s / 2, genJSONSub))
      a <- Gen.oneOf(
        Gen.listOf(j).map { l => val a = new JSONArray; l.foreach(s => a.asInstanceOf[java.util.List[Any]].add(s)); a },
        Gen.listOf(Gen.zip(arbitrary[String], j)).map{l =>
          val o = new JSONObject()
          l.foreach(x => o.asInstanceOf[java.util.Map[String, Any]].put(x._1, x._2))
          o
        })
    } yield a

    def genJSONSub: Gen[Any] =
      Gen.oneOf(arbitrary[String], arbitrary[Int], arbitrary[Boolean], arbitrary[Double], genJSON)
  }

  object JsonBug3 extends Properties("JsonBug3") {

    import com.eclipsesource.json._

    property("bug") = forAll { s: JsonValue =>
      JsonValue.readFrom(s.toString) =? s
    }

    implicit def ArbitaryJSONAware: Arbitrary[JsonValue] =
      Arbitrary(genJSON)

    def genJSON: Gen[JsonValue] = for {
      j <- Gen.sized(s => Gen.resize(s / 2, genJSONSub))
      a <- Gen.oneOf(
        Gen.listOf(j).map { l => val a = new JsonArray; l.foreach(s => a.add(s)); a },
        Gen.listOf(Gen.zip(arbitrary[String], j)).map{l =>
          val o = new JsonObject
          l.foreach(x => o.add(x._1, x._2))
          o
        })
    } yield a

    def genJSONSub: Gen[JsonValue] =
      Gen.oneOf(
        Gen.const(null).map(JsonValue.valueOf),
        arbitrary[String].map(JsonValue.valueOf),
        arbitrary[Int].map(JsonValue.valueOf),
        arbitrary[Boolean].map(JsonValue.valueOf),
        arbitrary[Double].map(JsonValue.valueOf),
        genJSON
      )
  }

  object CsvBug extends Properties("CsvBug") {

    import java.io._
    import com.googlecode.jcsv._
    import com.googlecode.jcsv.reader.internal._
    import com.googlecode.jcsv.writer.internal._
    import scala.collection.JavaConverters._

    property("bug") = forAllNoShrink(genData) { s: List[List[String]] => !s.isEmpty ==> {
      val writer = new StringWriter()
      val defaultWriter = new CSVWriterBuilder(writer)
        .strategy(new CSVStrategy(';', '\"', '#', false, false))
        .entryConverter(new DefaultCSVEntryConverter()).build()
      defaultWriter.writeAll(s.map(_.toArray).asJava)
      println(writer.toString)
      val r = CSVReaderBuilder.newDefaultReader(new StringReader(writer.toString))
      s =? r.readAll().asScala.toList.map(_.toList)
    }}

    def genData: Gen[List[List[String]]] = for {
      s <- Gen.nonEmptyListOf(Gen.listOfN(2,
         Gen.alphaStr.flatMap(s => Gen.oneOf(Gen.const(";"), Gen.const("\""), Gen.const("x#"), Gen.const("\t")))
      ))
    } yield s.map(_.filter(_.nonEmpty)).filter(_.nonEmpty)
  }

  object CodecBug extends Properties("CodecBug") {

    import org.apache.commons.codec._
    import org.apache.commons.codec.net._
    import org.apache.commons.codec.binary._

    property("binary") = forAll { b: List[Byte] =>
      binary(new QuotedPrintableCodec(), b) &&
        binary(new Base64(), b) &&
        binary(new URLCodec(), b) &&
        binary(new Hex(), b)
    }

    property("string") = forAll { s: String =>
      string(new QuotedPrintableCodec(), s) &&
        string(new URLCodec(), s) &&
        string(new BCodec(), s) &&
        string(new QCodec(), s)
    }

    def binary(se: BinaryDecoder with BinaryEncoder, s: List[Byte]): Prop =
      se.decode(se.encode(s.toArray)).toList ?= s

    def string(se: StringEncoder with StringDecoder, s: String): Prop =
      se.decode(se.encode(s)) ?= s
  }

  object CsvBug2 extends Properties("CsvBug2") {

    import java.io._
    import org.apache.commons.csv._
    import scala.collection.JavaConverters._

    property("binary") = forAllNoShrink(genData) { b: List[List[String]] =>
      val w = new StringWriter
      val x = CSVFormat.EXCEL.print(w)
      x.printRecords(b.map(_.asJava).asJava)
      val y = CSVFormat.EXCEL.parse(new StringReader(w.toString))
      y.getRecords.asScala.map {
        c => (0 until c.size()).map(i => c.get(i)).toList
      }.toList ?= b
    }

    def genData: Gen[List[List[String]]] =
      Gen.nonEmptyListOf(Gen.sized(i => Gen.listOfN(math.max(i, 2),
        Gen.const("a\uFFFEb")
//        arbitrary[String]
      )))
  }

}
