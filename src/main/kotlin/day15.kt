import java.io.File
import java.security.InvalidParameterException
import java.util.TreeMap
import java.util.TreeSet
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day15/test.txt"
  val realFile = "./src/main/resources/day15/input.txt"
  val sensorMap = SensorMap()
  File(realFile).forEachLine {
    sensorMap.parseLine(it)
  }
//  println( sensorMap.sensors[6].areaCovered())
  sensorMap.findEmptySpotsInRow(2000000)
 /* for (i in 0..4000000) {
    println("${LocalDateTime.now()} $i")
    sensorMap.findEmptySpotsInRow(i)
  }
  */
  val emptySpots = sensorMap.findEmptySpotsInRange(0, 4000000, 0, 4000000)
  println(emptySpots)
  for (i in 0..4000000) {
    val rangeList = emptySpots.getOrDefault(i, listOf())
    if (i % 10000 == 0) {
      println("row is $i")
    }
    if (rangeList.size < 2) {
      continue
    }
    for (j in 0 .. 4000000) {
      var found = false
      rangeList.forEach {
        if (it.range.contains(j)) {
          found = true
        }
      }
      if (!found) {
        println("found $i $j")
      }
    }
  }
}

class SensorMap {
  val  sensors = mutableListOf<Sensor>()
  val  beacons = mutableSetOf<MapPoint>()
  val points = mutableMapOf<Int, MutableMap<Int, MapItem>>()
  val lineRegex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

  fun parseLine(line: String) {
    val match = lineRegex.find(line)
    val sensorX = match!!.groupValues[1].toInt()
    val sensorY = match!!.groupValues[2].toInt()
    val beaconX = match!!.groupValues[3].toInt()
    val beaconY = match!!.groupValues[4].toInt()
    val beaconDist = Math.abs(sensorX - beaconX) + Math.abs(sensorY - beaconY)
    val sensor = Sensor(MapPoint(sensorX, sensorY), beaconDist)
    sensors.add(sensor)
    beacons.add(MapPoint(beaconX, beaconY))
    points.getOrPut(beaconX) { TreeMap()}.put(beaconY, MapItem.BEACON)
    points.getOrPut(sensorX) { TreeMap()}.put(sensorY, MapItem.SENSOR)
  }

  fun findEmptySpotsInRow(row: Int): Int {
    val emptyPoints = TreeSet<MapPoint>()
    sensors.forEach {
      val coveredPoint = it.pointsCoveredInRow(row)
    //  println("$it covers $coveredPoint in row $row")
      coveredPoint.forEach {
        if (it.y == row) {
          emptyPoints.add(it)
        }
      }
    }
 //   println(emptyPoints)
 //   println(emptyPoints.size)
    val filteredPoints =
      emptyPoints.filter { !points.getOrDefault(it.x, mutableMapOf()).getOrDefault(it.y, MapItem.EMPTY).equals(MapItem.BEACON) }
  //  println(filteredPoints.size)
    filteredPoints.forEach {
      points.getOrPut(it.x) { mutableMapOf() }.put(it.y, MapItem.COVERED)
    }
    return filteredPoints.size
  }

  fun findEmptySpotsInRange(minX: Int, maxX: Int, minY: Int, maxY: Int): Map<Int, List<SortableIntRange>> {
    val emptySpots = mutableMapOf<Int, MutableList<SortableIntRange>>()
    sensors.forEach {sensor ->
      println("Checking sensor $sensor")
      val coveredPoints = sensor.areaCovered(minX, maxX, minY, maxY)
      println("merging ranges ${emptySpots.size} ${coveredPoints.size}")
      coveredPoints.forEach{
        val yRanges = emptySpots.getOrPut(it.key) { mutableListOf() }
        yRanges.add(it.value)
        yRanges.sort()
        val mergedRanges = mutableListOf<SortableIntRange>()
        var currentRange = yRanges[0]
        for (i in 1 until yRanges.size) {
          if (currentRange.overlapsOrTouch(yRanges[i])) {
            currentRange = currentRange.merge(yRanges[i])
          } else {
            mergedRanges.add(currentRange)
            currentRange = yRanges[i]
          }
        }
        mergedRanges.add(currentRange)
        emptySpots[it.key] = mergedRanges
      }
    }
    return emptySpots
  }

  data class SortableIntRange(
    val range: IntRange
  ) : Comparable<SortableIntRange> {
    override fun compareTo(other: SortableIntRange): Int {
      if (range.start == other.range.start) {
        return range.endInclusive.compareTo(other.range.endInclusive)
      }
      return range.start.compareTo(other.range.start)
    }

    fun overlapsOrTouch(other: SortableIntRange): Boolean {
      return max(range.start,other.range.start)<=min(range.endInclusive,other.range.endInclusive) ||
        range.start + 1 == other.range.endInclusive ||
        other.range.start + 1 == range.endInclusive
    }

    fun merge(other: SortableIntRange): SortableIntRange {
      if (!overlapsOrTouch(other)) {
        throw InvalidParameterException("Cannot merge distinct ranges")
      }
      return SortableIntRange(min(range.start, other.range.start)..max(range.endInclusive, other.range.endInclusive))
    }
  }

  data class Sensor(
    val point: MapPoint,
    val beaconDist: Int
  ) {
    fun areaCovered(minX: Int, maxX: Int, minY: Int, maxY: Int): Map<Int,SortableIntRange> {
      val coveredArea = mutableMapOf<Int, SortableIntRange>()
      val startX = Math.max(minX, point.x - beaconDist)
      val endX = Math.min(maxX, point.x + beaconDist)
      for (i in startX .. endX) {
        val xdist = Math.abs(i - point.x)
        val ydist = Math.abs(beaconDist - xdist )
        val startY = Math.max(minY, point.y -ydist)
        val endY = Math.min(maxY, point.y + ydist)
        coveredArea.put(i, SortableIntRange(startY..endY))
      }
      return coveredArea
    }
    fun pointsCoveredInRow(row: Int): Set<MapPoint>{
      val emptyPoints = mutableSetOf<MapPoint>()
      val yDist = Math.abs(point.y - row)
      val xDist = beaconDist - yDist
      if (xDist >= 0) {
        for (i in -xDist..xDist) {
          emptyPoints.add(MapPoint(i + point.x, row))
        }
      }
      return emptyPoints
    }
  }


  data class MapPoint(
    val x: Int,
    val y: Int,
  ) : Comparable<MapPoint> {
    override fun compareTo(other: MapPoint): Int {
      if (x == other.x) {
        return y.compareTo(other.y)
      }
      return x.compareTo(other.x)
    }
  }

  enum class MapItem {
    EMPTY,
    BEACON,
    SENSOR,
    COVERED
  }
}