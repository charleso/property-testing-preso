class: center, bottom, heading-black
background-image: url(images/example-based.jpeg)

# Finding More Bugs With Less Effort

## @charlesofarrell

???

- Property-based testing is our default
- We have a majority of property-based tests

---

class: center, bottom, heading-black
background-image: url("images/chopsticks.jpg")

## @charlesofarrell

???

- Property-based testing is our default
- We have a majority of property-based tests





---

class: center, middle, heading-black
background-image: url(images/goal.jpeg)

## Motivation?

???

- Safety
- At Ambiata it costs $$$

---

class: center, middle, heading-black
background-image: url(images/example-based.jpeg)

## Example-based Testing

---

class: code

```scala
def substring(s: String, i: Int): String
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  substring("abc", 1) == "bc"
}
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  substring("abc", 1) == "bc"
  substring("abc", 3) == ""
}
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  substring("abc", 1) == "bc"
  substring("abc", 3) == ""
  substring("abc", 4) == ???
}
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  substring("abc", 1) == "bc"
  substring("abc", 3) == ""
  substring("abc", 4) == ???
  substring("abc", -1) == ???
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

<img src="images/jh.png" />

---

class: middle, center

<img src="images/jh.png" />
<img src="https://image.flaticon.com/icons/png/128/126/126157.png" />

---

class: center

<img src="images/volvo.jpg" width="100%" />

http://www.quviq.com/volvo-quickcheck/

---

- 3,000 pages of specifications
- 20,000 lines of QuickCheck
- 1,000,000 LOC, 6 suppliers
- 200 problems
- 100 problems in the standard

---

class: middle, center

### https://en.wikipedia.org/wiki/QuickCheck

> 31 Languages














---

class: top, left, heading-black
background-image: url(images/fuzzy.jpg)

## Fuzzing

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {



    substring("abc", 1) == "bc"
}
```

---

class: code

<pre><code class="scala scala-fg">def substring(s: String, i: Int): String

def testSubstring = {



    substring("abc", 1)
}
</code></pre>

```scala-bg






    substring("abc", 1) == "bc"
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {



    substring("abc", 1)
}
```

---

class: code

```scala-fg



  generateString      s


              s
```

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  generateString    { s =>


    substring(s, 1)
}}
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;


  forAll(         )





def forAll[A](Gen[A], A => Prop): Prop
</code></pre>

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>


    substring(s, 1)
}}

def forAll[A](Gen[A], A => Prop): Prop
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;


         genString







def genString: Gen[String]
</code></pre>

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>


    substring(s, 1)
}}

def forAll[A](Gen[A], A => Prop): Prop

def genString: Gen[String]
```

---

class: code

```scala-fg




         genInt    i

                 i






def genInt: Gen[Int]
```

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>

    substring(s, i)
}}}

def forAll[A](Gen[A], A => Prop): Prop

def genString: Gen[String]

def genInt: Gen[Int]
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>

    substring(s, i)
}}}
</code></pre>

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>

    substring(s, i)
}}}
```

---

class: center, middle, heading-white
background-image: url(images/learning.jpeg)

---

class: code


```scala
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>

    substring(s, i)
}}}
```

<pre><code class="warning">
! testSubstring: StringIndexOutOfBoundsException
    String index out of range: 0
> ARG_0: ""
> ARG_1: 0
</code></pre>

---

class: code

<pre><code class="scala scala-fg">&nbsp;




  i &lt; s.length ==>



def ==>(p: Boolean, Prop): Prop
</code></pre>

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>
  i < s.length ==>
    substring(s, i)
}}}

def ==>(p: Boolean, Prop): Prop
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>
  i < s.length ==>
    substring(s, i)
}}}
```

<pre><code class="warning">
! testSubstring: StringIndexOutOfBoundsException
    String index out of range: -1
> ARG_0: ""
> ARG_1: -1
</code></pre>

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>
  i >= 0 && i < s.length ==>
    substring(s, i)
}}}
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>
  i >= 0 && i < s.length ==>
    substring(s, i)
}}}
```

<pre><code class="success">
+ HelloWorld.substring: OK, passed 100 tests.
</code></pre>

---

class: code

<pre><code class="scala scala-fg">&nbsp;





    substring(s, i) == ???
</code></pre>

```scala-bg
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>
  i >= 0 && i < s.length ==>
    substring(s, i) == ???
}}}
```

















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

class: center, middle, heading-white
background-image: url(images/learning.jpeg)

---

class: code

```scala
import org.joda.time._


forAll(genDate) { dt =>

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


forAll(genDate) { dt =>

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

class: code

```scala
def toJson(user: User): Json

def fromJson(json: Json): Option[User]


forAll(genUser) { user =>

  val json = toJson(user)
  fromJson(json) == Some(user)
}
```

---

class: code

```scala
def toJson(user: User): Json

def fromJson(json: Json): Option[User]


forAll(genUser) { user =>

  val json = toJson(user)
  fromJson(json) == Some(user)
}
```

<pre><code class="warning">TODO
</code></pre>


---

class: code

```scala
def insertUser(u: User): UserId

def getUser(u: UserId): Option[User]
```

---

class: code

```scala
def insertUser(u: User): UserId

def getUser(u: UserId): Option[User]


forAll(genUser) { user =>

  val id = insertUser(user)
  getUser(id) == Some(user)
}
```

---

class: code

```scala
def insertUser(u: User): UserId

def getUser(u: UserId): Option[User]


forAll(genUser) { user =>

  val id = insertUser(user)
  getUser(id) == Some(user)
}
```

<pre><code class="warning">Some(User(\NULL)) != Some(User())
</code></pre>

---

background-image: url(images/learning.jpeg)















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


forAll(genList(genInt)) { l =>

  timSort(l) == bubbleSort(l)
}
```

---

class: code

```scala
def timSort(l: List[Int]): List[Int]


forAll(genList(genInt)) { l =>

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


forAll(genDate) { d =>

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

forAll(genDate) { d =>
forAll(genInt) { i =>

  dayPlus(d, i) ==
    d.toJoda.plusDays(i).fromJoda
}}
```

---

class: code

```scala
def dayPlus(d: Date, i: Int): Date = {
  ...
}

forAll(genDate) { d =>
forAll(genInt) { i =>

  dayPlus(d, i) ==
    d.toJoda.plusDays(i).fromJoda
}}
```

<pre><code class="warning">Date(2004,2,29) != Date(2004,3,1)

ARG_0: Date(2004,2,28)
ARG_1: 1
</code></pre>

---

class: center, middle, section-aqua, heading-white
background-image: url(images/property-based.jpeg)

---

class: code

```scala
def listUsersSortByName: List[User] =
  "SELECT * FROM user ORDER BY name ASC"
```

---

class: code

```scala
def listUsersSortByName: List[User]


forAll(genList(genUser)) { users =>

  users.foreach(u => insertUser(u))

  val l = listUsersSortByName

  l == users.sortBy(_.name.toLowerCase)
}
```

---

class: code

```scala
def findUsersByPostCode(code: Int): List[User] =
  "SELECT * FROM user WHERE postcode = ?"
```

---

class: code

```scala
def findUsersByPostCode(code: Int): List[User]

forAll(genPostcode) { postcode =>
forAll(genList(genUser)) { users =>

  val has = users.filter(_.postcode == postcode)

  users
    .foreach(u => userDb.insert(u))

  userDb.findUsersByPostCode(postcode) == has
}}
```

???

- God forbid you're not using prepared statements
  this would find a bug pretty quick

















---

class: center, middle

## Idempotence

<img src="images/idempotence.png" />

---

class: code

```scala
forAll(genList(genInt)) { s =>

  l.sorted.sorted == l.sorted
}
```

---

<img src="images/rust-bug.png" width="100%" />

---

<img src="images/rust-bug.png" width="100%" />

<img src="images/rust-idempotent.png" width="100%" />

> We do test for idempotency by running the 'target' of every test through rustfmt as a 'source'.
> Obviously, this only catches bugs if there is a relevant test.

???


https://github.com/rust-lang-nursery/rustfmt/issues/1668












---

class: center, middle, section-aqua, heading-white

## Invariants

???

- Need to be combined with multiple properties
  to test the entire function

---

class: code

```scala
forAll(genString) { s =>
  s.toLowerCase.length == s.length
}
```

---

class: code

```scala
forAll(genString) { s =>
  s.toLowerCase.length == s.length
}
```

<pre><code class="warning">
Expected 2 but got 1
ARG_0: "İ"
</code></pre>










---

class: top, center, heading-black
background-image: url(images/rebuild.jpg)

<h2 style="margin-top: 17px;">Rebuild</h2>

???

## Hard to prove, easy to verify

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genInt) { i =>

    substring(s, i) == ???
}}}
```

---

class: code

```scala
def substring(s: String, i: Int): String

def testSubstring = {
  forAll(genString) { s =>
  forAll(genString) { t =>

    substring(s + t, s.length) == t
}}}
```







---

class: right, bottom, heading-white
background-image: url(images/shrink.jpeg)

## Shrinking


---

class: code

```scala
forAll(genString) { s =>
  s.toLowerCase.length == s.length
}
```

<pre><code class="warning">
! testLowerCase: Falsified after 89 passed tests.

Expected 15 but got 14
ARG_0: "()*@#%KFPSDlİDcx;lk1&(#"
</code></pre>

---

class: code

```scala
forAll(genString) { s =>
  s.toLowerCase.length == s.length
}
```

<pre><code class="warning">
! testLowerCase: Falsified after 89 passed tests.

Expected 2 but got 1
ARG_0: "İ"
ARG_0_ORIGINAL: "()*@#%KFPSDlİDcx;lk1&(#"
</code></pre>









---

class: bottom, right, heading-white
background-image: url(images/state-based.jpeg)

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
def insertUser(u: User): UserId

def getUser(i: UserId): Option[User]
```

---

class: code

```scala
case class State(users: Map[UserId, User])
```

---

class: code

```scala
case class State(users: Map[UserId, User])

case class Insert(u: User) extends Commands
```

---

class: code

```scala
case class State(users: Map[UserId, User])

case class Insert(u: User) extends Commands {

  def nextState(state: State, id: UserId): State =
    state + (id -> u)

  def preCondition(state: State): Boolean =
    true

  def run: UserId =
    insertUser(u)
}
```

---

class: code

```scala
case class State(users: Map[UserId, User])

case class Insert(u: User) extends Commands

case class Get(u: UserId) extends Commands
```

---

class: code

```scala
case class State(users: Map[UserId, User])

case class Insert(u: User) extends Commands

case class Get(u: UserId) extends Commands

def genCommand: Gen[Command] =
  genOneOf(Insert, Get)
```

---

class: code

```scala
case class State(users: Map[UserId, User])

case class Insert(u: User) extends Commands

case class Get(u: UserId) extends Commands

def genCommand: Gen[Command] =
  genOneOf(Insert, Get)
```

<pre><code class="warning">
ARG_0: Actions(List(Insert, Insert, Get))
</code></pre>

---

class: code

```scala
case class State(users: Map[UserId, User])

case class Insert(u: User) extends Commands

case class Get(u: UserId) extends Commands

def genCommand: Gen[Command] =
  genOneOf(Insert, Get)
```

<pre><code class="warning">
ARG_0: Actions(List(Insert, Insert, Get))
ARG_0_ORIGINAL: Actions(List(Insert, Insert, Get, Get, Insert, Get))
</code></pre>

---

class: middle, center

<img src="images/threads-joke.png" />

---

class: code

```scala
trait Commands {

  def property(threadCount: Int): Prop
}
```

---

class: center, middle

<img src="http://blog.trifork.com/wp-content/uploads/2013/06/Riak_product_logo.png" />

> Poolboy had 85% test coverage (and most of the remaining 15% was irrelevant boilerplate) when I started QuickChecking it, and I felt pretty happy with its solidity, so I didn’t expect to find many bugs, if any. I was very wrong.

http://basho.com/posts/technical/quickchecking-poolboy-for-fun-and-profit/

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

class: center, middle, heading-white
background-image: url(images/generators.jpeg)

## Generators

???

- Investment!


---

class: code

```scala
forAll(genString) { s =>
forAll(genInt) { i =>


    substring(s, i)
}}
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;

i >= 0 && i &lt; s.length ==>
</code></pre>

```scala-bg
forAll(genString) { s =>
forAll(genInt) { i =>
i >= 0 && i < s.length ==>

    substring(s, i)
}}
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;
       choose(0, s.length - 1)





def choose(i: Int, b: Int): Gen[Int]
</code></pre>

```scala-bg
forAll(genString) { s =>
forAll(choose(0, s.length - 1)) { i =>


    substring(s, i)
}}

def choose(i: Int, b: Int): Gen[Int]
```

---

class: code

```scala
def testInsertUser =


    insert("bob")


def insert(user: String)
```

---

class: code

```scala
def testInsertUser =
  forAll(genString) { user =>

    insert(user)
  }
```

---

class: code

```scala
def testInsertUser =
  forAll(genString) { user =>

    insert(user)
  }
```

<pre><code class="warning">
! testInsertUser:
    Name cannot be blank
> ARG_0: ""
</code></pre>

---

class: code

<pre><code class="scala scala-fg">&nbsp;

  user != "" ==>
</code></pre>

```scala-bg
def testInsertUser =
  forAll(genString) { user =>
  user != "" ==>
    insert(user)
  }
```

---

class: code

```scala
def testInsertUser =
  forAll(genString) { user =>
  user != "" ==>
    insert(user)
  }
```

<pre><code class="warning">
! testInsertUser:
    Name cannot contain spaces
> ARG_0: "a b"
</code></pre>

---

class: code

<pre><code class="scala scala-fg">&nbsp;
         genList1(choose('a', 'z')
</code></pre>

```scala-bg
def testInsertUser =
  forAll(genList1(choose('a', 'z'))) { user =>

    insert(user)
  }
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;
         genUser









def genUser: Gen[String] =
  genList1(choose('a', 'z'))
</code></pre>

```scala-bg
def testInsertUser =
  forAll(genUser) { user =>

    insert(user)
  }






def genUser: Gen[String] =
  genList1(choose('a', 'z'))
```

---

class: code

<pre><code class="scala scala-fg">&nbsp;
         genUser





         genUser



def genUser: Gen[String] =
  genList1(choose('a', 'z'))
</code></pre>

```scala-bg
def testInsertUser =
  forAll(genUser) { user =>

    insert(user)
  }

def testDeleteUser =
  forAll(genUser) { user =>
    delete(user)
  }

def genUser: Gen[String] =
  genList1(choose('a', 'z'))
```

---

class: middle, center

<img src="images/exploits_of_a_mom.png" />

???

## True Story - "null" username

---

class: code


```scala







              "null"
```

---

class: code

```scala
def testMigrateUser =


    migrateUser("bob")


def testNullUsername =
  migrateUser("null")
```

???

- Only tests one code-path

---

class: code

```scala
def testMigration =
  forAll(genUser) { user =>

    migrateUser(user)
  }

def genUser: Gen[String] =

          genList1(choose('a', 'z'))
```

???

- Ideally you don't have to add any more tests

---

class: code

<pre><code class="scala scala-fg">&nbsp;






  genFrequency(

    1  -> genConst("null")
  )
</code></pre>

```scala-bg
def testMigration =
  forAll(genUser) { user =>

    migrateUser(user)
  }

def genUser: Gen[String] =
  genFrequency(
    19 -> genList1(choose('a', 'z'))
  , 1  -> genConst("null")
  )
```





















---

class: center, middle, heading-black
background-image: url(images/goal.jpeg)

## Motivation

---

class: center, middle, heading-black
background-image: url(images/example-based.jpeg)

## Example-based Testing

---

class: center, middle, section-aqua, heading-white
background-image: url(images/property-based.jpeg)

## Property-based Testing

???

## One Property / Many Bugs

---

class: center, middle, heading-white
background-image: url(images/learning.jpeg)

## Learning

---

class: center, middle, heading-white

<img src="images/patterns.jpg" style="width: 70%;" />

---

class: right, bottom, heading-white
background-image: url(images/shrink.jpeg)

## Shrinking

---

class: bottom, right, heading-white
background-image: url(images/state-based.jpeg)

## State-based Test

---

class: center, middle, heading-white
background-image: url(images/generators.jpeg)

## Generators

---

class: middle, center

### https://en.wikipedia.org/wiki/QuickCheck

> 31 Languages

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
