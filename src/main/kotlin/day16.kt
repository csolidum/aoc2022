import java.io.File

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day16/test.txt"
  val realFile = "./src/main/resources/day16/input.txt"
  val caveMap = CaveMap()
  File(testFile).forEachLine {
    caveMap.addValve(it)
  }
  caveMap.computeDistances()
  println(caveMap.distMap)
  val scenarioResult = caveMap.computeMaxPressureReleased("AA", setOf(), 30, false)
  println("result = $scenarioResult")
}

class CaveMap() {

  val valves = mutableMapOf<String, Valve>()
  val distMap = mutableMapOf<String, Map<String, Int>>()
  // Pairs are open valves + minutes left
  val seenScenarios = mutableMapOf<ScenarioKey, ScenarioResult>()

  private val lineRegex = "Valve (..) has flow rate=(\\d+); tunnel(?:s?) lead(?:s?) to valve(?:s?) (.+)".toRegex()
  fun addValve(line: String) {
    println(line)
    val match = lineRegex.find(line)
    val name = match!!.groupValues[1]
    val flowRate = match.groupValues[2].toInt()
    val connections = match.groupValues[3].split(", ")
    val valve = Valve(name, flowRate, connections)
    valves[name] = valve
    println(valve)
  }

  fun computeDistances() {
    valves.values.forEach {
      if (it.name == "AA" || it.flowRate > 0) {
        distMap[it.name] = it.computeDistances(valves)
      }
    }
  }

  fun computeMaxPressureReleased(startingPos:String, openValves: Set<String>, minutesLeft: Int, hasWorkingValve: Boolean): ScenarioResult {
    var maxPressure = 0
    var timeTilNextValve = minutesLeft
    var biggestPath = listOf<String>()

    if (minutesLeft <= 0) {
      return ScenarioResult(0, biggestPath)
    }
    if (seenScenarios.containsKey(ScenarioKey(openValves, minutesLeft, startingPos))) {
      return seenScenarios[ScenarioKey(openValves, minutesLeft, startingPos)]!!
    }
    val distances = distMap[startingPos]!!
    var newOpenValves = openValves
    if (hasWorkingValve) {
      newOpenValves = newOpenValves.plus(startingPos)
    }
    val remainingValves = distances.filter { !newOpenValves.contains(it.key) }
    if (remainingValves.size > 0) {
      remainingValves.forEach {
        var newMinutesLeft = minutesLeft - distances.get(it.key)!!
        val dist = distances.get(it.key)!!
        var timeTurningValve = 0
        if (hasWorkingValve) {
          newMinutesLeft -= 1
          timeTurningValve = 1
        }
        var curPressure = (dist) * valves[startingPos]!!.flowRate
        openValves.forEach {
          curPressure += (dist + 1) * valves[it]!!.flowRate
        }
        val scenarioResult = computeMaxPressureReleased(it.key, newOpenValves, newMinutesLeft, true)
        curPressure += scenarioResult.pressureReleased
        if (maxPressure < curPressure) {
          maxPressure = curPressure
          biggestPath = scenarioResult.flowPath
          timeTilNextValve = distances.get(it.key)!! + timeTurningValve
        }
      }
    } else {
      openValves.forEach {
        maxPressure += minutesLeft * valves[it]!!.flowRate
      }
      maxPressure += (minutesLeft - 1) * valves[startingPos]!!.flowRate
      biggestPath = listOf(startingPos)
    }
    /*
    maxPressure += (timeTilNextValve - 1) * valves[startingPos]!!.flowRate
    openValves.forEach{
      maxPressure += timeTilNextValve * valves[it]!!.flowRate
    }

     */
    // calculate pressure for current valves and
    val scenarioResult = ScenarioResult(maxPressure, listOf(startingPos).plus(biggestPath))
    seenScenarios[ScenarioKey(openValves, minutesLeft, startingPos)] = scenarioResult
    println("Computed ${ScenarioKey(openValves, minutesLeft, startingPos)} to have $scenarioResult $timeTilNextValve")
    return scenarioResult
  }
  data class Valve(
    val name: String,
    val flowRate: Int,
    val connections: Collection<String>
  ){
    fun computeDistances(valves: Map<String, Valve>): Map<String, Int> {
      val explored = mutableSetOf<String>(name)
      val frontier = mutableListOf<Pair<Valve, Int>>(Pair(valves[name]!!, 0))
      val distances = mutableMapOf<String, Int>()
      while(frontier.size > 0) {
        val nextVal = frontier.removeFirst()
        val valve = nextVal.first
        val valveDist = nextVal.second
        if (valve.flowRate > 0) {
          distances[valve.name] = valveDist
        }
        println(valve)
        valve.connections.forEach {
          if (!explored.contains(it)) {
            frontier.add(Pair(valves[it]!!, valveDist + 1))
            explored.add(it)
          }
        }

      }
      return distances
    }
  }

  data class ScenarioKey(
    val openValves: Set<String>,
    val minutesLeft: Int,
    val startingPos: String
  )
  data class ScenarioResult(
    val pressureReleased: Int,
    val flowPath: List<String>
  )
}