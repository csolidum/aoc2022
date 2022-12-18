import java.io.File

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day6/test.txt"
  val realFile = "./src/main/resources/day6/input.txt"
  var line :String = ""
  File(realFile).forEachLine {
    line = it
  }
  for (i in 0..(line.length - 1)) {
    val set = line.substring(i, i+14).toSet()
    println(set)
    if (set.size == 14) {
      println(line[i+14])
      println(i + 14)
      break
    }
  }
}