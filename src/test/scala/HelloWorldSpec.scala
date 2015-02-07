import org.specs2._

class HelloWorldSpec extends Specification with ScalaCheck { def is =

  "List reverse" ! prop { (a: String) =>
    a.reverse.reverse ==== a
  }

  "list reverse 2" ! prop { (a: String, b: String) =>
    (b + a).reverse ==== (a.reverse + b.reverse)
  }
}
