import java.util.Optional
import java.io.File
import java.lang.Exception
import java.util.TreeMap
fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day14/test.txt"
  val realFile = "./src/main/resources/day14/input.txt"
  val sandMap = SandMap()
  File(realFile).forEachLine {
    val linePoints = SandMap.MapPoint.parseLine(it)
    println(linePoints)
    for (i in 1 until linePoints.size) {
      sandMap.addLine(linePoints[i-1], linePoints[i])
      println("adding ${linePoints[i-1]} to ${linePoints[i]}")
      println(sandMap.path)
    }
  }
  println(sandMap.path)
  println("${sandMap.minX} ${sandMap.minY} ${sandMap.maxX} ${sandMap.maxY}")
  println(sandMap.pathToString())
  var numDrops = 0;
  while(sandMap.dropSand()) {
  //  println()
  //  println(sandMap.pathToString())
    numDrops += 1
  }
  println(numDrops)

  val sandMap2 = SandMap()
  File(realFile).forEachLine {
    val linePoints = SandMap.MapPoint.parseLine(it)
    println(linePoints)
    for (i in 1 until linePoints.size) {
      sandMap2.addLine(linePoints[i-1], linePoints[i])
    }
  }
  var numSandPoints = 0
  sandMap2.addLine(SandMap.MapPoint( sandMap2.minX - 200, sandMap2.maxY+2), SandMap.MapPoint(sandMap2.maxX + 200, sandMap2.maxY + 2))
  println(sandMap2.pathToString())

  while(!sandMap2.checkPos(500, 0)) {
    numSandPoints++
    if(!sandMap2.dropSand()) {
      throw Exception("sand should not fall infinitately")
    }
    println("dropping sand $numSandPoints")
  }
  println(sandMap2.path.keys)
  println(sandMap2.pathToString())
  println(numSandPoints)


}

class SandMap (){
  val sandPoint = MapPoint(500, 0)
  var maxX = Int.MIN_VALUE
  var maxY = Int.MIN_VALUE
  var minX = Int.MAX_VALUE
  var minY = Int.MAX_VALUE

  // Index is y, x to make calculating fall better
  val path = TreeMap<Int, MutableMap<Int, Boolean>>()

  fun addLine(a: MapPoint, b: MapPoint) {
    val endX = Math.max(a.x, b.x)
    val startX = Math.min(a.x, b.x)
    val endY = Math.max(a.y, b.y)
    val startY = Math.min(a.y, b.y)

    if (maxX < endX) {
      maxX = endX
    }
    if (maxY < endY) {
      maxY = endY
    }
    if (minX > startX) {
      minX = startX
    }
    if (minY > startY) {
      minY = startY
    }
    for (i in startY..endY) {
      if (!path.containsKey(i)) {
        path[i] = TreeMap()
      }
      val pathRow = path!![i]
      for (j in startX..endX) {
        pathRow!![j] = true
      }
    }
  }

  fun addPoint(point: MapPoint) {
    if (!path.containsKey(point.y)) {
      path[point.y] = TreeMap()
    }
    path!![point.y]!![point.x] = true

    if (maxX < point.x) {
      maxX = point.x
    }
    if (minX > point.x) {
      minX = point.x
    }
    if (maxY < point.y) {
      maxY = point.y
    }
    if (minY > point.y) {
      minY = point.y
    }
  }

  fun pathToString(): String {
    var pathStr = ""
    for (i in (minY - 1) .. (maxY + 1)) {
      for (j in (minX - 1) .. (maxX + 1)) {
        if (path.containsKey(i) && path[i]!!.containsKey(j)) {
          pathStr += "#"
        } else {
          pathStr += "."
        }
      }
      pathStr += "\n"
    }
    return pathStr
  }

  fun dropSand(): Boolean {
    var sandPos = sandPoint.copy()
    while (sandPos.y < maxY) {
      if (!checkPos(sandPos.x ,sandPos.y + 1)) {
        sandPos = MapPoint(sandPos.x, sandPos.y + 1)
      } else if (!checkPos(sandPos.x - 1, sandPos.y + 1)) {

        sandPos = MapPoint(sandPos.x - 1, sandPos.y + 1)
      } else if (!checkPos(sandPos.x +   1, sandPos.y + 1)) {

        sandPos = MapPoint(sandPos.x + 1, sandPos.y + 1)
      } else {

        addPoint(sandPos)
        return true
      }
    }

    return false
  }

  fun checkPos(x: Int, y: Int): Boolean {
    return path.getOrDefault(y, TreeMap()).getOrDefault(x, false)
  }
  data class MapPoint(
    val x: Int,
    val y: Int
  ){
    companion object {
      private val lineRegex = "(\\d+),(\\d+)".toRegex()
      fun parseLine(line: String): List<MapPoint> {
        val matches = lineRegex.findAll(line)
        val points = mutableListOf<MapPoint>()

        matches.forEach {
          points.add(MapPoint(it!!.groupValues[1].toInt(), it!!.groupValues[2].toInt()))
        }
        return points
      }
    }
  }

}