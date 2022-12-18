import java.io.File

val file = "./src/main/resources/day4/input.txt"

fun main(args: Array<String>) {
  val elfPairs = parseFile(file)
  println(elfPairs)
  var numCompleteOverlaps = 0
  var numPartialOverlap = 0
  elfPairs.forEach {pair ->
    if (pair.completeOverlap()) {
      numCompleteOverlaps += 1
    }
    if (pair.someOverlap()) {
      numPartialOverlap += 1
    }
    println(pair.completeOverlap())
  }
  println(numCompleteOverlaps)
  println(numPartialOverlap)
}

fun parseFile(filename: String): List<ElfPair> {
  var elfPairs = listOf<ElfPair>()
  File(filename).forEachLine {
    val pairs = it.split(",")
    elfPairs = elfPairs.plus(ElfPair(getAssignment(pairs[0]), getAssignment(pairs[1])))
  }
  return elfPairs
}

fun getAssignment(s: String): List<Int> {
  val p = s.split('-')
  return (p[0].toInt()..p[1].toInt()).toList()
}
class ElfPair (
  firstAssignment: List<Int>,
  secondAssignment: List<Int>
){
  private val firstElf = firstAssignment.toSet()
  private val secondElf = secondAssignment.toSet()

  fun completeOverlap(): Boolean {
    val intersect =  firstElf.intersect(secondElf)
    return (intersect.size == firstElf.size) || (intersect.size == secondElf.size)
  }

  fun someOverlap() :Boolean {
    val intersect =  firstElf.intersect(secondElf)
    return intersect.size > 0
  }
  object Helper {

  }
}