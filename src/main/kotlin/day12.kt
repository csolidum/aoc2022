import java.io.File
import java.util.Optional

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day12/test.txt"
  val realFile = "./src/main/resources/day12/input.txt"
  var map = mutableListOf<Array<Int>>()
  var startX = 0
  var startY = 0
  var endX = 0
  var endY = 0
  File(realFile).forEachLine { line ->
    val row = Array(line.length) {
      if (line[it] == 'S') {
        startX = map.size
        startY = it
        0
      } else if (line[it] == 'E') {
        endX = map.size
        endY = it
        25
      } else {
        line[it].code - 'a'.code
      }
    }
    map.add(row)
    for (i in row) {
      print("$i ")
    }
    println()
  }
  val elfHill = ElfHill(
    Pair(startX, startY),
    Pair(endX, endY),
    map
  )
  val distances = elfHill.findPath()
  var max = 0
  distances.forEach {
    it.forEach {
      if (it.isPresent) {
        val dist = it.get()
        if (dist > max) {
          max = dist
        }
        print("$dist ")
      } else {
        print("_ ")
      }
    }
    println()
  }
  println(distances[endX][endY])

  var minDistance = Int.MAX_VALUE
  for (i in 0 until map.size) {
    for (j in 0 until map[i].size) {
      if (map[i][j] == 0) {
        val elfHill = ElfHill(
          Pair(i, j),
          Pair(endX, endY),
          map
        )
        val dist = elfHill.findPath()[endX][endY]
        if (dist.isPresent && dist.get() < minDistance) {
          minDistance = dist.get()
        }
      }
    }
  }
  println(minDistance)
}

class ElfHill(
  val start: Pair<Int, Int>,
  val end: Pair<Int, Int>,
  val elevations: List<Array<Int>>
) {
  fun findPath(): Array<Array<Optional<Int>>> {
    val dist = Array<Array<Optional<Int>>>(elevations.size) {
      Array<Optional<Int>>(elevations[it].size) {
        Optional.empty()
      }
    }
    dist[start.first][start.second] = Optional.of(0)
    val frontier = mutableListOf<Pair<Int, Int>>()
    frontier.add(start)
    while (frontier.size > 0) {
      println(frontier)
      val pos = frontier.removeFirst()
      val x = pos.first
      val y = pos.second
      val curHeight = elevations[x][y]
      val curDist = dist[x][y].get()
      if (x == end.first && y == end.second) {
        break
      }
      if (isValidPos(x-1, y) && dist[x-1][y].isEmpty) {
        if (curHeight + 1 >=  elevations[x-1][y]) {
          dist[x-1][y] = Optional.of(curDist + 1)
          frontier.add(Pair(x-1,y))
        }
      }
      if (isValidPos(x+1, y) && dist[x+1][y].isEmpty) {
        if (curHeight + 1 >=  elevations[x+1][y]) {
          dist[x+1][y] = Optional.of(curDist + 1)
          frontier.add(Pair(x+1,y))
        }
      }
      if (isValidPos(x, y - 1) && dist[x][y - 1].isEmpty) {
        if (curHeight + 1 >=  elevations[x][y - 1]) {
          dist[x][y - 1] = Optional.of(curDist + 1)
          frontier.add(Pair(x,y - 1))
        }
      }
      if (isValidPos(x, y + 1) && dist[x][y + 1].isEmpty) {
        if (curHeight + 1 >=  elevations[x][y + 1]) {
          dist[x][y + 1] = Optional.of(curDist + 1)
          frontier.add(Pair(x,y + 1))
        }
      }
    }
    return dist
  }

  private fun isValidPos(x: Int, y: Int): Boolean {
    return (x >= 0) && (x < elevations.size) && (y >= 0) && (y < elevations[0].size)
  }
}