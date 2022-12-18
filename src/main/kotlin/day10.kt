import java.io.File

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day10/test.txt"
  val test2File = "./src/main/resources/day10/test2.txt"
  val realFile = "./src/main/resources/day10/input.txt"
  val clock = Clock()
  File(realFile).forEachLine { line ->
    if (line == "noop") {
      clock.noop()
    } else {
      val addVal = line.substring(5).toInt()
      clock.addX(addVal)
    }
  }
  println(clock.signalSum)
  for (drawnLine in clock.drawnLines) {
    drawnLine.forEach {
      print(it)
    }
    println("")
  }
}

class Clock(){
  var cycle = 1
  var register = 1

  val specialCycles = setOf(20, 60, 100, 140, 180, 220)
  var signalSum = 0
  val drawnLines = mutableListOf<Array<Char>>()
  var currentLine = Array<Char>(40) { ' ' }

  fun checkCycle() {
    println("cycle $cycle: ${(cycle - 1) % 40} $register")
    if (specialCycles.contains(cycle)) {
      println("signal strength $cycle: ${cycle * register}")
      signalSum += cycle * register
    }
    if ((cycle - 1) % 40 == 0) {
      currentLine = Array(40) { ' '}
      drawnLines.add(currentLine)
    }
    var pixelX = (cycle - 1) % 40
    var pixelVal = '.'
    if (Math.abs(register - pixelX) <= 1) {
      pixelVal = '#'
    }
    println("pixelVal is $pixelVal")
    currentLine[pixelX] = pixelVal
  }

  fun noop() {
    checkCycle()
    cycle += 1
  }

  fun addX(x: Int) {
    checkCycle()
    cycle += 1
    checkCycle()
    cycle += 1
    register += x
  }
}