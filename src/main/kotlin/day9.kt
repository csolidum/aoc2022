import java.io.File
import java.util.ArrayDeque
import java.util.TreeMap

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day9/test.txt"
  val test2File = "./src/main/resources/day9/test2.txt"
  val realFile = "./src/main/resources/day9/input.txt"
  val regex = "(.) (\\d+)".toRegex()
  val rope = Array<RopePosition>(10) {
    RopePosition(0,0)
  }
  val head = rope[0]
  val seen = TreeMap<Int, TreeMap<Int, Boolean>>()
  File(realFile).forEachLine { line ->
    val match = regex.find(line)
    val dir = match!!.groupValues[1]
    val dist = match!!.groupValues[2].toInt()
    for (i in 0 until dist) {
      when (dir) {
        "L" -> head.moveLeft()
        "R" -> head.moveRight()
        "U" -> head.moveUp()
        "D" -> head.moveDown()
      }
      for (i in 1 until rope.size) {
        rope[i].follow(rope[i-1])
      }
      val tail = rope[rope.size-1]
      if (!seen.containsKey(tail.x)) {
        seen.put(tail.x, TreeMap<Int, Boolean>())
      }
      seen.get(tail.x)!!.put(tail.y, true)
      println("X: ${head.x} ${head.y} Y: ${tail.x} ${tail.y}")
    }
  }
  var spacesSeen = 0
  seen.values.forEach{
    it.values.forEach{
      spacesSeen += 1
    }
  }
  println(spacesSeen)
}

data class RopePosition(
  var x: Int,
  var y: Int
) {
  fun moveRight() {
    x += 1
  }

  fun moveLeft() {
    x -= 1
  }

  fun moveUp() {
    y += 1
  }

  fun moveDown() {
    y -= 1
  }

  fun follow(head: RopePosition) {
    val deltaX = x - head.x
    val deltaY = y - head.y
    println("$deltaX $deltaY")
    if (Math.abs(deltaX) < 2 && Math.abs(deltaY) < 2) {
      return
    }
    if (Math.abs(deltaX) == 2) {
      val moveX = deltaX / Math.abs(deltaX)
      x -= moveX
      if (Math.abs(deltaY) == 1) {
        val moveY = deltaY / Math.abs(deltaY)
        y -= moveY
      }
    }
    if (Math.abs(deltaY) == 2) {
      val moveY = deltaY / Math.abs(deltaY)
      y -= moveY
      if (Math.abs(deltaX) == 1) {
        val moveX = deltaX / Math.abs(deltaX)
        x -= moveX
      }
    }
  }
}