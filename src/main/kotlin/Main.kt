import java.io.File

fun main(args: Array<String>) {
  val fileName = "./src/main/resources/day1/input.txt"
  val path = System.getProperty("user.dir")
  var numElves = 0
  var calorieCounter = 0
  val elfMap = mutableMapOf<Int, Int>()
  println(path)
  File(fileName).forEachLine {
    println(it)
    if (it.isBlank()) {
      elfMap.put(numElves, calorieCounter)
      calorieCounter = 0
      numElves += 1
    } else {
      calorieCounter += it.toInt()
    }
  }
  elfMap.put(numElves, calorieCounter)
  var maxCals = 0
  elfMap.values.forEach() {
    if (it > maxCals) {
      maxCals = it
    }
  }
  println(elfMap.toString())
  println(maxCals)
  println(elfMap.values.sortedDescending())
  println("finished")
}