import java.io.File

fun main(args: Array<String>) {
 // Helper.findDuplicate(args)
  Helper.findBadge()
}
val fileName = "./src/main/resources/day3/input.txt"

object Helper {
  fun findDuplicate(args: Array<String>) {
    var priritySum = 0
    File(fileName).forEachLine {
      val packSize = it.length/2
      println(it)
      val firstPack = it.substring(0,packSize ).toSet()
      val secondPack = it.substring(packSize).toSet()
      val intersect = firstPack.intersect(secondPack)
      println(intersect)
     intersect.forEach {
       priritySum += it.priority()
     }
    }
    println(priritySum)
  }

  fun findBadge() {
    var sets = arrayOf<Set<Char>>()
    File(fileName).forEachLine {
      sets = sets.plus(it.toSet())
      println(sets.size)
    }
    var sum = 0
    for (i in 0..(sets.size/3 - 1)) {
      val intersect = sets[i*3].intersect(sets[(i*3)+1]).intersect(sets[(i*3)+2])
      println(intersect)
      sum += intersect.toList()[0].priority()
    }
    println(sum)
  }


  fun Char.priority(): Int {
    if (this.isUpperCase()) {
      return this.code - 'A'.code + 27
    } else {
      return this.code - 'a'.code + 1
    }
  }
}