import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day11/test.txt"
  val realFile = "./src/main/resources/day11/input.txt"
  var currentMonkey = mutableListOf<String>()
  var monkeys = mutableListOf<Monkey>()
  File(realFile).forEachLine { line ->
    if (line.length == 0) {
      val monkey = Monkey.parseMonkey(currentMonkey)
      monkeys.add(monkey)
      println(monkey)
      currentMonkey = mutableListOf<String>()
    } else {
      currentMonkey.add(line)
    }
  }
  for (i in 0 until 10000) {
    monkeys.forEach {
      it.examineObjects(monkeys)
    }
    /*
    monkeys.forEach {
      println("$i ${it.inspected} $it")
    }
     */
    if (i % 100 == 0 ) {
      println(i)
    }
  }
  var shenanigans = monkeys.map{ it.inspected}.sortedDescending()
  println(shenanigans)
  println(shenanigans[0] * shenanigans[1])
}

data class Monkey(
  val id: Int,
  var items: MutableList<Long>,
  val ops: MonkeyOp,
  val test: Long,
  val passTest: Int,
  val failTest: Int
) {
  var inspected = 0L
  fun examineObjects(monkeys: List<Monkey>) {
    items.forEach {
      inspected += 1
      var newWorry = ops.eval(it) % 2007835830
      val pass = ((newWorry % test) == 0L)
      var newMonkey = failTest
      if (pass) {
        newMonkey = passTest
      }
    //  println("Monkey $id passes $it to $newMonkey with new value $newWorry")

      monkeys[newMonkey].items.add(newWorry)
    }
    items = mutableListOf()
  }
 companion object {
   fun parseMonkey(lines: List<String>): Monkey {
     val id = lines[0].substring(7, 8).toInt()
     val items = lines[1].substring(18).split(", ").map { it.toLong() }.toMutableList()
     val op = parseFunc(lines[2].substring(19))
     val test = lines[3].substring(21).toLong()
     val pass = lines[4].substring(29).toInt()
     val fail = lines[5].substring(30).toInt()

     return Monkey(
       id,
       items,
       op,
       test,
       pass,
       fail
     )
   }
   val opRegex = "(.*) (.) (.*)".toRegex()
   fun parseFunc(exp: String): MonkeyOp {
     val opMatch = opRegex.find(exp)
     return MonkeyOp(
       opMatch!!.groupValues[1],
       opMatch!!.groupValues[2],
       opMatch!!.groupValues[3]
     )
   }
 }
  data class MonkeyOp(
    val firstNum: String,
    val op: String,
    val secondNum: String,
  ){
    fun eval(old: Long): Long {
      var first = old
      var second = old
      if (firstNum != "old") {
        first = firstNum.toLong()
      }
      if (secondNum != "old") {
        second = secondNum.toLong()
      }
      if (op == "+") {
        return first + second
      } else {
        return first * second
      }
    }
  }
 }

