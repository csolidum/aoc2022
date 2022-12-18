import java.io.File

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day8/test.txt"
  val realFile = "./src/main/resources/day8/input.txt"
  var width = 0
  var height = 0
  var forest = mutableListOf<MutableList<Int>>()
  File(realFile).forEachLine { line ->
    height += 1
    val treeLine = mutableListOf<Int>()
    line.forEach { digit ->
      treeLine.add(digit.toString().toInt())
    }
    forest.add(treeLine)
    width = treeLine.size
  }
  println(forest)
  println("$height $width")

  var seenGrid = Array<Array<Boolean>>(height) {
    Array<Boolean>(width) {
      false
    }
  }
  for (i in 0..(height-1)) {
    var maxHeight = Int.MIN_VALUE
    for (j in 0..(width-1)) {
      if (forest!!.get(i)!!.get(j) > maxHeight) {
        seenGrid[i][j] = true
        maxHeight = forest!!.get(i)!!.get(j)
      }
    }
    maxHeight = Int.MIN_VALUE
    for (j in  (0..(width-1)).reversed()) {
      if (forest!!.get(i)!!.get(j) > maxHeight) {
        seenGrid[i][j] = true
        maxHeight = forest!!.get(i)!!.get(j)
      }
    }
  }
  // calculate seen from edige
  for (j in 0..(width-1)) {
    var maxHeight = Int.MIN_VALUE
    for (i in 0..(height-1)) {
      if (forest!!.get(i)!!.get(j) > maxHeight) {
        seenGrid[i][j] = true
        maxHeight = forest!!.get(i)!!.get(j)
      }
    }
    maxHeight = Int.MIN_VALUE
    for (i in  (0..(height-1)).reversed()) {
      if (forest!!.get(i)!!.get(j) > maxHeight) {
        seenGrid[i][j] = true
        maxHeight = forest!!.get(i)!!.get(j)
      }
    }
  }
  for (i in 0..(height-1)) {
    var maxHeight = Int.MIN_VALUE
    for (j in 0..(width-1)) {
      if (forest!!.get(i)!!.get(j) > maxHeight) {
        seenGrid[i][j] = true
        maxHeight = forest!!.get(i)!!.get(j)
      }
    }
    maxHeight = Int.MIN_VALUE
    for (j in (width-1)..0) {
      if (forest!!.get(i)!!.get(j) > maxHeight) {
        seenGrid[i][j] = true
        maxHeight = forest!!.get(i)!!.get(j)
      }
    }
  }
  var numSeen = 0
  seenGrid.forEach {
    it.forEach {
      print("$it ")
      if (it) {
        numSeen++
      }
    }
    println()
  }
  println(numSeen)


  val seenUp = Array<Array<Int>>(height) { i ->
    Array<Int>(width) { j ->
      var canSee = 0
      for (k in (0 until i).reversed()) {
        if (forest[i]!!.get(j) > forest!!.get(k)!!.get(j)) {
          canSee++
        } else {
          canSee++
          break
        }
      }
      canSee
    }
  }
  val seenDown = Array<Array<Int>>(height) { i ->
    Array<Int>(width) { j ->
      var canSee = 0
      for (k in ((i+1) until height)) {
        if (forest!!.get(i)!!.get(j) > forest!!.get(k)!!.get(j)) {
          canSee++
        } else {
          canSee++
          break
        }
      }
      canSee
    }
  }
  val seenLeft = Array<Array<Int>>(height) { i ->
    Array<Int>(width) { j ->
      var canSee = 0
      for (k in (0 until j).reversed()) {
        if (forest!!.get(i)!!.get(j) > forest!!.get(i)!!.get(k)) {
          canSee++
        } else {
          canSee++
          break
        }
      }
      canSee
    }
  }
  val seenRight = Array<Array<Int>>(height) { i ->
    Array<Int>(width) { j ->
      var canSee = 0
      for (k in ((j+1) until width)) {
        if (forest!!.get(i)!!.get(j) > forest!!.get(i)!!.get(k)) {
          canSee++
        } else {
          canSee++
          break
        }
      }
      canSee
    }
  }

  val sceneScore = Array<Array<Int>>(height) {i ->
    Array<Int>(width) { j ->
      seenRight[i][j] * seenLeft[i][j] * seenUp[i][j] * seenDown[i][j]
    }
  }
  var maxScore = 0
  sceneScore.forEach {
    it.forEach {
      print("$it ")
      if (it > maxScore) {
        maxScore = it
      }
    }
    println()
  }
  println(maxScore)

}

