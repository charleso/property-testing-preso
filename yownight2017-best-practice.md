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

---

class: middle

> "Program testing can be used to show the presence of bugs, but never to show their absence!" - Edsger W. Dijkstra

---

class: center, middle, section-aqua, heading-white

## Why is testing hard?

---

class: code

```scala
def doStuff(x: Bool): String
```

<pre><code class="warning">2 possible inputs
</code></pre>

---

class: code

```scala
def doStuff(x: Bool): String

def testDoStuff = {
  doStuff(true) === ...
  doStuff(false) === ...
}
```

<pre><code class="warning">2 possible inputs
</code></pre>

---

class: code

```scala
def doStuff(x: Bool, y: Int): String

def testDoStuff = {
  doStuff(true, 0) === ...
  doStuff(true, -1) === ...
  doStuff(true, 1) === ...
  doStuff(false, 1) === ...
}
```

<pre><code class="warning">2 * 4.3e9 possible inputs
</code></pre>

---

class: middle, center

<img src="images/threads-joke.png" />

---

class: center, middle, section-aqua, heading-white

## Example-based Testing

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

class: code

```scala
def doStuff(x: Bool, y: Int): String

def testDoStuff =
  forAll { (x : Bool, y: Int) =>
    doStuff(x, y) == ???
  }
```

---

class: code

```scala
def doStuff(x: Bool, y: Int): String

def testDoStuff =
  forAll { (x : Bool, y: Int) =>
    doStuff(x, y) == ???
  }
```

<pre><code class="warning">
+ testDoStufff: OK, passed 100 tests.
</code></pre>








---

class: center, middle, section-aqua, heading-white

## Shrinking

---

class: code

```scala
def doStuff(x: Bool, y: Int): String

def testDoStuff =
  forAll { (x : Bool, y: Int) =>
    doStuff(x, y) == ???
  }
```

<pre><code class="warning">
! testDoStuff: Falsified after 2 passed tests.
> Labels of failing property:
Expected "a" but got "b"
> ARG_0: true
> ARG_1: 10
</code></pre>

---

class: code

```scala
def doStuff(x: Bool, y: Int): String

def testDoStuff =
  forAll { (x : Bool, y: Int) =>
    doStuff(x, y) == ???
  }
```
<pre><code class="warning">
! testDoStuff: Falsified after 2 passed tests.
> Labels of failing property:
Expected "a" but got "b"
> ARG_0: true
> ARG_1: 10
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
def toBytes(s: String, c: Charset): Array[Byte]

def fromBytes(b: Array[Bytes], c: Charset): String
```

---

class: code

```scala
def toBytes(s: String, c: Charset): Array[Byte]

def fromBytes(b: Array[Bytes], c: Charset): String


forAll { (s: String, c: Charset) =>
  val b = toBytes(s, c)
  fromBytes(b, c) == s
}
```

---

class: code

```scala
def toBytes(s: String, c: Charset): Array[Byte]

def fromBytes(b: Array[Bytes], c: Charset): String


forAll { (s: String, c: Charset) =>
  val b = toBytes(s, c)
  fromBytes(b, c) == s
}
```

<pre><code class="warning">"돪" != "?"

> ARG_0: "돪"
> ARG_1: windows-1252
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


forAll { (postcode: Int, users: List[User]) =>

  users.foreach(u => userDb.insert(u))

  val has = users.filter(_.postcode == postcode)

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


forAll { (postcode: Int, u1: List[User], u2: List[User]) =>

  val has = u1.map(_.copy(postcode = postcode))

  val without = u2.filter(_.postcode != postcode)

  (has ++ without).foreach(u => userDb.insert(u))

  userDb.findByPostCode(postcode) == has
}
```

---

class: code

```scala
forAll { users: List[Users] =>

  val out = hadoopJob(users)

  val expected =
    users.groupBy(_.name)
         .mapValues(_.sortBy(_.postcode))

  out == expected
}
```

???

- Hadoop
- Horrible Java APIs
- Byte manipulation
















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

???

- TODO MORE EXAMPLES








---

class: center, middle, section-aqua, heading-white

## State-based Testing

???

- "State of the art"

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

## Getting Started

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
forAll { (name: String, postcode: Int) =>

  val user = User(name, postcode)

  db.insert(user)
}
```

---

class: code


```scala
forAll { (name: String, postcode: Int) =>

  val user = User(name, postcode)

  db.insert(user)
}
```

<pre><code class="warning">
Invalid username: ""
Invalid postcode: -1
</code></pre>

---

class: center, middle, section-aqua, heading-white

## Generators

???

- Investment!

---

class: code

```scala
def genUsername: Gen[Username] =
  Gen.nonEmptyListOf(Gen.alphaChar)
    .map(Username.fromString)

def genPostcode: Gen[Postcode] =
  Gen.choose(1000, 9999)
    .map(Postcode.fromInt)
```

???

- Yes it doesn't compile
  - Just ignore

---

class: code

```scala
def genUsername: Gen[Username] =
  Gen.nonEmptyListOf(Gen.alphaChar)
    .map(Username.fromString)

def genPostcode: Gen[Postcode] =
  Gen.choose(1000, 9999)
    .map(Postcode.fromInt)


forAll { (u: Username, p: Postcode) => ... }

forAll { (u1: Username, u2: Username) => ... }

forAll { p: List[Postcode] => ... }
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
def testNullUsername = {

  dbMigration.migrate("null")
}
```

???

- Only tests one code-path

---

class: code

```scala
def genUsername: Gen[Username] =

  Gen.frequency(
    19 -> Gen.alphaStr
  , 1  -> Gen.const("null")
  ).map(Username.fromString)
```

???

- Ideally you don't have to add any more tests




















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

## Example-based Testing

---

class: center, middle, section-aqua, heading-white

## Property-based Testing

---

class: center, middle, section-aqua, heading-white

## Shrinking

---

class: center, middle, section-aqua, heading-white

## Patterns

- Round-trip
- Test Oracle
- Idempotence
- Invariants
- State-based

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

## Links

- "Choosing properties for property-based testing"
  - http://fsharpforfunandprofit.com/posts/property-based-testing-2/
- John Hughes - "Testing the Hard Stuff and Staying Sane"
  - https://www.youtube.com/watch?v=zi0rHwfiX1Q
- Alex Chan - "Property-based Testing in Practice"
  - https://www.infoq.com/presentations/hypothesis-afl-property-testing
- [@charlesofarrell](https://twitter.com/charlesofarrell)
  - https://github.com/charleso/property-testing-preso
