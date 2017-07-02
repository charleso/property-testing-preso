class: center, bottom, heading-black
background-image: url("images/chopsticks.jpg")

# Finding More Bugs With Less Effort

## @charlesofarrell

???

- Property-based testing is our default
- We have a majority of property-based tests





---

class: center, middle, section-aqua, heading-white

## Motivation?

#### &nbsp;

---

class: center, middle, section-aqua, heading-white

## Motivation?

#### Writing correct software

???

- Safety
- At Ambiata it costs $$$

---

class: middle

> "Program testing can be used to show the presence of bugs, but never to show their absence!" - Edsger W. Dijkstra

---

class: center, middle, section-aqua, heading-white

## Example-based Testing

---

class: code

```scala
def reverse[A](l: List[A]): List[A]
```

???

- The "Hello World" of property based testing

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  reverse(List(1, 2, 3)) == List(3, 2, 1)


}
```

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  reverse(List(1, 2, 3)) == List(3, 2, 1)
  reverse(List()) == List()

}
```

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  reverse(List(1, 2, 3)) == List(3, 2, 1)
  reverse(List()) == List()
  reverse(List(1, 1, 3)) == List(3, 1, 1)
}
```

---

class: middle, center

<img src="images/qa-engineer-tweet.png" />







---

class: middle, center

<img src="images/quickcheck-paper.png" />

???

- Year 2000!

---

class: middle, center

### https://en.wikipedia.org/wiki/QuickCheck

> 31 Languages

---

class: center, middle, section-aqua, heading-white

## Hello World

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {

    reverse(?) == ???
}
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;


  forAll (???)




def forAll[A](g: Gen[A], a => Result): Result
</code></pre>

```scala-bg
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (???)             { l =>
    reverse(l) == ???
  }
}

def forAll[A](g: Gen[A], a => Result): Result
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;


          genList(???)






def genList[A](g: Gen[A]): Gen[List[A]]
</code></pre>

```scala-bg
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(???))    { l =>
    reverse(l) == ???
  }
}

def forAll[A](g: Gen[A], a => Result): Result

def genList[A](g: Gen[A]): Gen[List[A]]
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;


                  genInt








def genInt: Gen[Int]
</code></pre>

```scala-bg
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(l) == ???
  }
}

def forAll[A](g: Gen[A], a => Result): Result

def genList[A](g: Gen[A]): Gen[List[A]]

def genInt: Gen[Int]
```

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(l) == ???
  }
}
```

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(reverse(l)) == l
  }
}
```

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(reverse(l)) == l
  }
}
```

<pre><code class="warning">
+ testReverse: OK, passed 100 tests.
</code></pre>

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(reverse(l)) == l
  }
}
```

<pre><code class="warning">
List()
List(2147483647, 1)
List(-2147483648, 1, -1094275287)
List(6569, 2147, 14801, 0, 1852, -9217, 0)
</code></pre>








---

class: right, bottom, heading-black
background-image: url(http://media.istockphoto.com/photos/one-small-dollar-picture-id172411676)

## Shrinking


---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(reverse(l)) == l
  }
}
```

<pre><code class="warning">
! testReverse: Falsified after 2 passed tests.

  List(0, -1, 2147, 2090, -1, 0) !=
  List(-2147, -1, -1, 0, 0, 2090)

> ARG_0: List(0, -1, 2090, -2147, -1, 0)
</code></pre>

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(reverse(l)) == l
  }
}
```
<pre><code class="warning">
! testReverse: Falsified after 2 passed tests.

  List(0, -1) !=
  List(-1, 0)

> ARG_0: List(0, -1)
</code></pre>












---

background-image: url(http://media.istockphoto.com/photos/climber-woman-standing-in-front-of-a-stone-rock-outdoor-picture-id480310380?s=2048x2048)

---

class: code

```scala
def reverse[A](l: List[A]): List[A]

def testReverse = {
  forAll (genList(genInt)) { l =>
    reverse(l) == ???
  }
}
```

<pre><code class="warning">
1. Property Testing
2. ???
3. Profit
</code></pre>


















---

class: center

<img src="images/patterns.jpg" style="width: 70%;" />

???

- Patterns
- Not just writing list sorting functions
- Can be hard to get started








---

class: middle, center

## Round-trip

<img src="images/property-inverse.png" />

---

class: code

```scala
def toBytes(s: String): Array[Byte]

def fromBytes(b: Array[Bytes]): String
```

---

class: code

```scala
def toBytes(s: String): Array[Byte]

def fromBytes(b: Array[Bytes]): String


forAll(genString) { s =>
  val b = toBytes(s)
  fromBytes(b) == s
}
```

---

class: code

```scala
def toBytes(s: String): Array[Byte]

def fromBytes(b: Array[Bytes]): String


forAll(genString) { s =>
  val b = toBytes(s)
  fromBytes(b) == s
}
```

<pre><code class="warning">"돪" != "?"

> ARG_0: "돪"
</code></pre>

---

class: code

```scala
def toJson(user: User): Json

def fromJson(json: Json): Option[User]
```

---

class: code

```scala
def toJson(user: User): Json

def fromJson(json: Json): Option[User]


forAll { user: User =>

  fromJson(toJson(user)) == Some(user)
}
```

---

class: code

```scala
def insert(u: User): UserId

def get(u: UserId): Option[User]
```

---

class: code

```scala
def insert(u: User): UserId

def get(u: UserId): Option[User]


forAll { u: User =>

  val id = userDb.insert(u)

  userDb.get(id) == Some(u)
}
```

---

class: code

```scala
def insert(u: User): UserId

def get(u: UserId): Option[User]


forAll { u: User =>

  val id = userDb.insert(u)

  userDb.get(id) == Some(u)
}
```

<pre><code class="warning">Some(User(\NULL)) != Some(User())
</code></pre>

---

background-image: url(https://d2lm6fxwu08ot6.cloudfront.net/img-thumbs/960w/2O4DGFJ1Y5.jpg)















---

class: middle, center

## Test Oracle

<img src="images/model-based.png" />

---

class: code

```scala
def timSort(l: List[Int]): List[Int]
```

---

class: code

```scala
def timSort(l: List[Int]): List[Int]


forAll { l: List[Int] =>

  timSort(l) == bubbleSort(l)
}
```

---

class: code

```scala
def timSort(l: List[Int]): List[Int]


forAll { l: List[Int] =>

  timSort(l) == bubbleSort(l)
}
```

<pre><code class="warning">
https://bugs.openjdk.java.net/browse/JDK-8072909

TimSort fails with ArrayIndexOutOfBounds
on worst case long arrays
</code></pre>

---

class: code

```scala
case class Date(value: Int)


def toJoda(date: Date): JodaDate

def fromJoda(date: JodaDate): Date
```

---

class: code

```scala
case class Date(value: Int)


def toJoda(date: Date): JodaDate

def fromJoda(date: JodaDate): Date


forAll { d: Date =>

  d.toJoda.fromJoda == d
}
```

---

class: code

```scala
def dayPlus(d: Date, i: Int): Date = {
  ...
}
```

---

class: code

```scala
def dayPlus(d: Date, i: Int): Date = {
  ...
}

forAll { (d: Date, i: Int) =>

  dayPlus(d, i) ==
    d.toJoda.plusDays(i).fromJoda
}
```

---

class: code

```scala
def dayPlus(d: Date, i: Int): Date = {
  ...
}

forAll { (d: Date, i: Int) =>

  dayPlus(d, i) ==
    d.toJoda.plusDays(i).fromJoda
}
```

<pre><code class="warning">Date(2004,2,29) != Date(2004,3,1)

ARG_0: Date(2004,2,28)
ARG_1: 1
</code></pre>

---

class: code

```scala
def listSortByName: List[User] =
  "SELECT * FROM user ORDER BY name ASC"
```

---

class: code

```scala
def listSortByName: List[User]


forAll { users: List[User] =>

  users.foreach(u => userDb.insert(u))

  val l = userDb.listSortByName

  l == users.sortBy(_.name.toLowerCase)
}
```

---

class: code

```scala
def findByPostCode(postcode: Int): List[User] =
  "SELECT * FROM user WHERE postcode = ?"
```

---

class: code

```scala
def findByPostCode(postcode: Int): List[User]

forAll { (postcode: Int
  , users: List[User]) =>

  val has = users.filter(_.postcode == postcode)



  users
    .foreach(u => userDb.insert(u))

  userDb.findByPostCode(postcode) == has
}
```

???

- God forbid you're not using prepared statements
  this would find a bug pretty quick

---

class: code

```scala
def findByPostCode(postcode: Int): List[User]

forAll { (postcode: Int
  , u1: List[User], u2: List[User]) =>

  val has = u1.map(_.copy(postcode = postcode))

  val not = u2.filter(_.postcode != postcode)

  (has ++ not)
    .foreach(u => userDb.insert(u))

  userDb.findByPostCode(postcode) == has
}
```
















---

class: center, middle

## Idempotence

<img src="images/idempotence.png" />

---

class: code

```scala
forAll { s: List[Int] =>

  l.distinct.distinct == l.distinct
}

forAll { l: List[Int] =>

  l.sorted.sorted == l.sorted
}
```

---

class: code

```scala
forAll { u: User =>

  val r1 = userDb.createOrUpdate(u)

  val r2 = userDb.createOrUpdate(u)

  r1 == r2
}
```

---

class: code

```scala
forAll { m: Migration =>

  db.migrate(m)
  val s1 = db.schema()

  db.migrate(m)
  val s2 = db.schema()

  s1 == s2
}
```














---

class: center, middle, section-aqua, heading-white

## Invariants

???

- Need to be combined with multiple properties
  to test the entire function

---

class: code

```scala
forAll { s: String =>
  s.toLowerCase.length == s.length
}
```

---

class: code

```scala
forAll { s: String =>
  s.toLowerCase.length == s.length
}
```

<pre><code class="warning">
Expected 2 but got 1
ARG_0: "İ"
</code></pre>

---

- TODO MORE EXAMPLES








---

class: center, middle, section-aqua, heading-white

## State-based Testing

???

- "State of the art"
- Unit test vs integration testing
- Generate the whole test

---

## State-based Testing

1. Describe the possible states
2. Describe what actions can take place in each state
3. Describe how to tell if the state is correct
4. Have the computer try lots of random actions – look for a breaking combination

---

class: code

```scala
def insert(u: User): Id

def get(i: Id): Option[User]
```

---

class: code

```scala
case class State(users: Map[Id, User])
```

---

class: code

```scala
case class State(users: Map[Id, User])

case class Insert(u: User) extends Commands
```

---

class: code

```scala
case class State(users: Map[Id, User])

case class Insert(u: User) extends Commands {

  def nextState(state: State, id: Id): State =
    state + (id -> u)

  def preCondition(state: State): Boolean =
    true

  def run: Id =
    insert(u)
}
```

---

class: code

```scala
case class State(users: Map[Id, User])

case class Insert(u: User) extends Commands

case class Get(u: Id) extends Commands
```

---

class: code

```scala
case class State(users: Map[Id, User])

case class Insert(u: User) extends Commands

case class Get(u: Id) extends Commands

def genCommand: Gen[Command] =
  Gen.oneOf(Insert, Get)
```

---

class: code

```scala
case class State(users: Map[Id, User])

case class Insert(u: User) extends Commands

case class Get(u: Id) extends Commands

def genCommand: Gen[Command] =
  Gen.oneOf(Insert, Get)
```

<pre><code class="warning">
ARG_0: Actions(List(Insert, Insert, Get))
</code></pre>

---

class: middle, center

<img src="images/threads-joke.png" />

---

class: code

```scala
final def property(threadCount: Int): Prop
```

---

class: middle, center

<img src="images/jh.png" />

> &nbsp;

???

- John Hughes
- Inventor of QuickCheck
- Now makes money from testing other people's code

- 3,000 pages of specifications
- 20,000 lines of QuickCheck
- 1,000,000 LOC, 6 suppliers
- 200 problems
- 100 problems in the standard

---

class: center

<img src="images/volvo.jpg" width="100%" />

http://www.quviq.com/volvo-quickcheck/

---

class: middle, center

<img src="images/leveldb.png" />

http://www.quviq.com/google-leveldb/


???

- LevelDB is a fast key-value storage C++ library written at Google that
  provides an ordered mapping from string keys to string values.

---

class: code, thinner

```sh
1. open new database
2. put key1 and val1
3. close database
4. open database
5. delete key2
6. delete key1
7. close database
4. open database
5. delete key2
6. delete key1
7. close database
11. open database
12. put key3 and val1
13. close database
14. open database
15. close database
16. open database
17. seek first
```

???

- 17 steps.
- THEN 33 steps

https://groups.google.com/forum/#!topic/leveldb/gnQEgMhxZAs

- Was in 2013



































---

class: center, middle

## Fuzzing

---

class: code

```scala
def testUser = {



    val user = User("bob", 2000)

    db.insert(user)
}
```

---

class: code

```scala
def testUser = {
  forAll(getString) { name =>
  forAll(getInt) { postcode =>

    val user = User(name, postcode)

    db.insert(user)
}}}
```

---

class: code


```scala
def testUser = {
  forAll(getString) { name =>
  forAll(getInt) { postcode =>

    val user = User(name, postcode)

    db.insert(user)
}
```

<pre><code class="warning">
! testUser: Falsified after 21 passed tests.
NullPointerException
> ARG_0: ""
> ARG_1: 0
</code></pre>

---

class: center, middle, section-aqua, heading-white

## Generators

???

- Investment!

---

class: code

```scala
def genUsername: Gen[String] =
  Gen.nonEmptyListOf(Gen.alphaChar)

def genPostcode: Gen[Int] =
  Gen.choose(1000, 9999)
```

???

- Yes it doesn't compile
  - Just ignore

---

class: code

```scala
def genUsername: Gen[String] =
  Gen.nonEmptyListOf(Gen.alphaChar)

def genPostcode: Gen[Int] =
  Gen.choose(1000, 9999)


forAll(genUsername, genPostcode) { ... }

forAll(genUsername, genUsername) { ... }

forAll(genList(genUsername)) { ... }
```

???

- We can add more properties using the _same_ generator

---

class: middle, center

<img src="images/exploits_of_a_mom.png" />

???

## True Story - "null" username

---

class: code

```scala
def testMigration = {


    dbMigration.migrate("bob")

}

def testNullUsername = {
  dbMigration.migrate("null")
}
```

???

- Only tests one code-path

---

class: code

```scala
def testMigration = {
  forAll(genUsername) { u =>

    dbMigration.migrate(u)
  }
}

def genUsername: Gen[String] =
  Gen.frequency(
    19 -> Gen.alphaStr
  , 1  -> Gen.const("null")
  )
```

???

- Ideally you don't have to add any more tests

---

class: code

```scala
def testMigration = {
  forAll(genUsername) { u =>

    dbMigration.migrate(u)
  }
}

def testInsert = {
  forAll(genUsername) { u =>

    dbUser.insert(u)
  }
}
```




















---

class: center, middle, section-aqua, heading-white

## Bug hunting

---

## [@markhibberd](https://twitter.com/markhibberd)

<i style="font-size: 26px">
"Maybe you could test a relatively well known open source library and find a
bug for something they have unit tests for"
</i>

---

## [@markhibberd](https://twitter.com/markhibberd)

<i style="font-size: 26px">
"Maybe you could test a relatively well known open source library and find a
bug for something they have unit tests for"
</i>

<img src="images/challenge.jpg" style="position: absolute; width: 600px; right: 0px; bottom: 0px;" />

---

class: code

```scala
import org.joda.time._


forAll { dt: DateTime =>

  val formatter = DateTimeFormat.fullDateTime()

  val s = formatter.print(dt)

  formatter.parseDateTime(s) == dt
}
```

???

- Symmetrical

---

class: code

```scala
import org.joda.time._


forAll { dt: DateTime =>

  val formatter = DateTimeFormat.fullDateTime()

  val s = formatter.print(dt)

  formatter.parseDateTime(s) == dt
}
```

<pre><code class="warning">Invalid format:
"Sunday, September 22, 2148 9:08:08 PM ART"
is malformed at "ART"
</code></pre>

---

## Bug or Feature?

- http://stackoverflow.com/questions/15642053/joda-time-parsing-string-throws-java-lang-illegalargumentexception
- http://comments.gmane.org/gmane.comp.java.joda-time.user/1385
- https://github.com/JodaOrg/joda-time/commit/14863a51230b3d44201646dbc1ce5d7f6bb97a33

---

class: image, middle

<a href="https://twitter.com/da_terry/status/587602658011189252">
  <img src="images/alex.png" />
</a>

---

class: code

```scala
import org.rauschig.jarchivelib._


def archiveProp(archiver: ArchiverFactory): Prop =

  forAll { files: Map[FilePath, Array[Byte]] =>

    val archive = archiver.create(files)

    archive.loadAll() == files
  }
```

???

- Symmetrical

---

class: code

```scala
import org.rauschig.jarchivelib._


def archiveProp(archiver: ArchiverFactory): Prop =

  forAll { files: Map[FilePath, Array[Byte]] =>

    val archive = archiver.create(files)

    archive.loadAll() == files
  }

archiveProp(createArchiver(Jar, PACK200))
```

<pre><code class="warning">Expected "" but got "PKPACK2000"
</code></pre>

---

class: code

```scala
import org.rauschig.jarchivelib._


def archiveProp(archiver: ArchiverFactory): Prop =

  forAll { files: Map[FilePath, Array[Byte]] =>

    val archive = archiver.create(files)

    archive.loadAll() == files
  }

archiveProp(createArchiver(Tar))
```

<pre><code class="warning">file name '...' is too long (> 100 bytes)
</code></pre>

---

class: code

```scala
import org.rauschig.jarchivelib._


def archiveProp(archiver: ArchiverFactory): Prop =

  forAll { files: Map[FilePath, Array[Byte]] =>

    val archive = archiver.create(files)

    archive.loadAll() == files
  }

archiveProp(createArchiver(Ar))
```

<pre><code class="warning">Expected "aⲐ" "a?"
</code></pre>







---

class: center, middle, section-aqua, heading-white

## Motivation: Writing Correct Software

---

class: center, middle, section-aqua, heading-white

## Example-based Testing

---

class: center, middle, section-aqua, heading-white

## Property-based Testing

---

class: center, middle, section-aqua, heading-white

## One Property Can Find Multiple Bugs

---

class: center, middle, heading-white
background-image: url(https://d2lm6fxwu08ot6.cloudfront.net/img-thumbs/960w/2O4DGFJ1Y5.jpg)

## Learning

---

class: center, middle, section-aqua, heading-white

## Shrinking

---

class: center, middle, section-aqua, heading-white

## Patterns

---

class: center, middle, section-aqua, heading-white

## State-based

---

class: center, middle, section-aqua, heading-white

## Generators

---

class: center, middle, section-aqua, heading-white

## Fun!

???

- No longer feels like you're writing tests for test sake
- Enjoyable tot think about your system invariants

---

class: middle, center

### https://en.wikipedia.org/wiki/QuickCheck

> 31 Languages

---

## Links

- "Choosing properties for property-based testing"
  - http://fsharpforfunandprofit.com/posts/property-based-testing-2/
- John Hughes - "Testing the Hard Stuff and Staying Sane"
  - https://www.youtube.com/watch?v=zi0rHwfiX1Q
- Alex Chan - "Property-based Testing in Practice"
  - https://www.infoq.com/presentations/hypothesis-afl-property-testing
- [@charlesofarrell](https://twitter.com/charlesofarrell)
  - https://github.com/charleso/property-testing-preso
