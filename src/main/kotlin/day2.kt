import java.io.File

fun main(args: Array<String>) {
  val fileName = "./src/main/resources/day2/input.txt"
  val scoreGuide = mapOf<String, Map<String, Int>>(
    "A" to mapOf<String, Int>("X" to 4, "Y" to  8, "Z" to 3),
    "B" to mapOf<String,Int>("X" to 1, "Y" to 5, "Z" to 9),
    "C" to mapOf<String,Int>("X" to 7, "Y" to 2, "Z" to 6)
  )
  val scoreGuide2 = mapOf<String, Map<String, Int>>(
    "A" to mapOf<String, Int>("X" to 3, "Y" to  4, "Z" to 8),
    "B" to mapOf<String,Int>("X" to 1, "Y" to 5, "Z" to 9),
    "C" to mapOf<String,Int>("X" to 2, "Y" to 6, "Z" to 7)
  )
  var litScore = 0
  var rigScore = 0

  File(fileName).forEachLine {
    val moves = it.split(" ")
    println(moves[0])
    println(moves[1])
    println(scoreRound(moves[0], moves[1], scoreGuide2))
    litScore += scoreRound(moves[0],moves[1],scoreGuide)
    rigScore += scoreRound(moves[0], moves[1], scoreGuide2)
  }
  println(litScore)
  println(rigScore)
}

fun scoreRound(op: String, mine: String, rules :Map<String, Map<String, Int>>): Int {
  return rules[op]!![mine]!!
}