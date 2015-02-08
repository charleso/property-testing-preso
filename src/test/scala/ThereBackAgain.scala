import java.nio.CharBuffer
import java.nio.charset.Charset

import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import org.joda.time.{DateTimeZone, DateTime}
import org.scalacheck.Prop._
import org.scalacheck.{Gen, Arbitrary, Properties}

import scala.io.Codec
import scala.collection.JavaConverters._

object ThereBackAgain extends Properties("ThereBackAgain") {

  property("codec") = forAll { (s: String, c: Codec) =>
    c.decoder.decode(c.encoder.encode(CharBuffer.wrap(s))).toString =? s
  }

  implicit def CodecArbitrary: Arbitrary[Codec] =
    Arbitrary(Gen.oneOf(Charset.availableCharsets.values.asScala.map(new Codec(_)).toSeq))

  property("date zones") = forAll { (dt: DateTime, dtz: DateTimeZone) =>
     dt.toDateTime(dtz).toDateTime(dt.getChronology) =? dt
  }

  property("date offset") = forAll { (dt: DateTime, dtz: DateTimeZone, i: Int) =>
    dt.toDateTime(dtz).plusDays(i).minusDays(i) =? dt.toDateTime(dtz)
  }

  property("date parsing 1") = forAll { (dt: DateTime, dtz: DateTimeZone) =>
    val formatter = DateTimeFormat.fullDateTime().withZone(dtz)
    val dt2 = dt.withMillisOfSecond(0).withZone(dtz)
    formatter.parseDateTime(formatter.print(dt2)) =? dt2
  }

  property("date parsing 2") = forAll { (dt: DateTime, dtz: DateTimeZone) =>
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZone(dtz)
    val dt2 = dt.withMillisOfSecond(0).withZone(dtz)
    formatter.parseDateTime(formatter.print(dt2)) =? dt2
  }

  implicit def DateArbitrary: Arbitrary[DateTime] =
    Arbitrary(Gen.choose(0, 1000L * 60 * 60 * 24 * 365 * 1000).map(new DateTime(_)))

  implicit def DateTimeZoneArbitrary: Arbitrary[DateTimeZone] =
    Arbitrary(Gen.oneOf(DateTimeZone.getAvailableIDs.asScala.map(DateTimeZone.forID).toSeq))
}
