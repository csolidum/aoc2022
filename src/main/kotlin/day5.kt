import java.io.File

fun main(args: Array<String>) {
  val numColumns = 9
  val testFile = "./src/main/resources/day5/test.txt"
  val realFile = "./src/main/resources/day5/input.txt"
  var parsingState = true
  val stack = Stack(numColumns)
  var moves = listOf<Move>()
  val regex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
  File(realFile).forEachLine { line ->
    println(line)
    println(line.length)
    if (line.length == 0 ) {
      parsingState = false
      return@forEachLine
    }
    if (line[1] == '1') {
      return@forEachLine
    }
    if (parsingState) {
      var c = 0
      for (i in 1..line.length step 4) {
        println(line[i])
        if (line[i] != ' ') {
          stack.append(c, line[i])
        }
        c += 1
      }
    } else {
      val match = regex.find(line)
      val m = Move(match!!.groupValues[1].toInt(), match!!.groupValues[2].toInt() -1 , match!!.groupValues[3].toInt() -1)
      moves = moves.plus(m)
      println(m.print())
    }
  }
  stack.print()

  moves.forEach { move ->
    val elements = stack.take(move.src, move.amt)
    /*
    elements.forEach{ c->
      stack.push(move.dest, c)
    }
    */
    stack.pushAll(move.dest, elements)
    println(stack.print())
    println()
  }
}

class Stack(
  numColumns: Int
) {
  val columns = Array<MutableList<Char>>(numColumns){ mutableListOf() }

  fun append(i: Int, c: Char) {
    columns[i].add(c)
  }

  fun push(i: Int, c: Char) {
    columns[i].add(0, c)
  }

  fun pushAll(i: Int, c: List<Char>) {
    columns[i].addAll(0, c)
  }

  fun print() {
    columns.forEach {
      println(it)
    }
  }

  fun take(i: Int, amt: Int): List<Char>
  {
    val l = columns[i].take(amt)
    for (t in 1..amt) {
      columns[i].removeFirst()
    }
    return l
  }
}

class Move(
  amt: Int,
  src: Int,
  dest: Int
){
  val amt = amt
  val src = src
  val dest = dest

  fun print() {
    println("$amt $src $dest")
  }
}