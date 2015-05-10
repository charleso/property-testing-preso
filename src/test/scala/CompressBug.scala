import java.io.{FileOutputStream, File}
import java.nio.file.{Paths, Path, Files}

import org.rauschig.jarchivelib.{Archiver, ArchiveFormat, ArchiverFactory}
import org.scalacheck.Prop._
import org.scalacheck._

object CompressBug extends Properties("CompressBug") {

  include(CompressBug1)

  object CompressBug1 extends Properties("CompressBug1") {

//    property("xx") = {
//
//      val createArchiver: Archiver = ArchiverFactory.createArchiver(ArchiveFormat.AR)
//      createArchiver.create("foo", new File("."), new File("./a" + Paths.get("\u2C90").toString))
//      val x = createArchiver.stream(new File("foo.ar"))
//      println(x.getNextEntry.getName)
//      x.close()
//      true
//    }
    /*
    property("bug") = {

      val n = "\u1B43"
      withDir {
       d =>
//                  val f = new File(d, n)
//                  f.createNewFile()
//                  println(f.exists())
//                  f.delete()
//                  println(f.exists())
//                  f.exists()
        val path: Path = Paths.get(d.getAbsolutePath, n)
         path.toFile.createNewFile()
         val f2 = d.listFiles()(0)
         println(f2.toPath.getFileName + " " + f2.getName)
//        Files.delete(f2.toPath)
        println(f2.exists())
        println(path.toFile.exists())
//         path.toFile.delete()
        println(path.toFile.exists())
          path.toFile.exists()
      }
    }
    */
//    property("bug") = {
//      val n = "\uFABC"
//      println(java.text.Normalizer.normalize(n, java.text.Normalizer.Form.NFD) + "  " +  java.text.Normalizer.normalize(n, java.text.Normalizer.Form.NFC))
//      Paths.get(n).toString ?= n
//    }
    property("bug") = forAll { m: List[(String, Array[Byte])] => m.nonEmpty ==> {

//    property("bug2") = {
//      val m = List("\u1B43" -> "".getBytes())
//      val m = List("\uFFFE" -> "".getBytes())
//      val n = "\uFABC"
//      val s = "".getBytes("UTF-8")
      val archiver = ArchiverFactory.createArchiver(ArchiveFormat.SEVEN_Z)
      withDir { f =>
        val in = m.toMap.toList.map { case (n, s) => Paths.get("a" + n
          .replace("/", "")
          .replace("\\", "")
          .replace("\uFFFE", "")
          .replace("\uFFFF", "")
          .replace("\0", "")
        ).toString -> s.toList }
        in.foreach {
          x =>
            val path = Paths.get(f.getAbsolutePath, x._1)
            path.toFile.getParentFile.mkdirs()
//            Files.write(path, x._2.toArray)
            val out = new FileOutputStream(new File(f.getAbsolutePath, x._1))
            out.write(x._2.toArray)
            out.close()
//            println(x._1 + " " + path.toFile.exists() + " " + new File(f, x._1).exists())
//            println(path.toFile.exists() + " " + new File(f, x._1).exists())
        }
        val l = List.newBuilder[(String, List[Byte])]
        try {
//          val archive = archiver.create("foo", f, in.map(x => new File(f, x._1)): _*)
          val archive = archiver.create("foo", f, f.listFiles(): _*)
//          println(archive.getAbsolutePath)
            val stream = archiver.stream(archive)
          try {
            while (stream.getNextEntry != null) {
              val e = stream.getCurrentEntry
//              println(e.getName)
              val out = Stream.continually(stream.read).takeWhile(-1 != _).map(_.toByte).toArray
              l += (e.getName -> out.toList)
            }
          } finally {
            stream.close()
            archive.delete()
          }
          in.sortBy(_._1) ?= l.result().sortBy(_._1)
        }
      }
    }}
  }

  def withDir[A](f: File => A): A = {
    val f2 = File.createTempFile("file", "test")
    f2.delete()
    f2.mkdirs()
    try {
      f(f2)
    } finally {

      if (!deleteDir(f2)) {
        println("COULD NOT DELETE: " + f2.getAbsolutePath)
      }
    }
  }

  def deleteDir(f: File): Boolean = {
    if (f.isDirectory) {
      f.listFiles().foreach(deleteDir)
    }
    f.delete()
//    Files.delete(f.toPath)
//    !f.exists()
  }
}
