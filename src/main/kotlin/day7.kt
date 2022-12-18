import java.io.File
import java.util.ArrayDeque

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day7/test.txt"
  val realFile = "./src/main/resources/day7/input.txt"
  val baseDir = SysDir("/")
  var curDir = baseDir
  val dirStack  = ArrayDeque<SysDir>()
  val fileSizeRegex = "(\\d+) (.+)".toRegex()
  dirStack.push(baseDir)
  File(realFile).forEachLine { line ->
    println(line)
    if (line == "$ cd /" || line == "$ ls") {
      println("got cd root")
      return@forEachLine
    }
    if (line == "$ cd ..") {
      println("got move back")
      dirStack.pop()
      curDir = dirStack.peek()
    } else if (line.startsWith("$ cd ")) {
      val newDir = line.substring(5)
      println("Moving to $newDir")
      dirStack.push(curDir.dirs!!.get(newDir))
      curDir = dirStack.peek()
    } else if (line.startsWith("dir ")) {
      val dirName = line.substring(4)
      println("Creating dir $dirName")
      curDir.addDir(dirName)
    } else {
      val match = fileSizeRegex.find(line)
      val fileSize = match!!.groupValues[1].toInt()
      val fileName = match!!.groupValues[2]
      println("Creating file $fileName $fileSize")
      curDir.addFile(fileName, fileSize)
    }
  }
  val probSize = baseDir.probSize()
  println("The probsize is $probSize")

  val curSize = baseDir.size()
  val spaceNeeded = curSize - baseDir.driveNeeded
  println("Current size is $curSize need to free $spaceNeeded")
  println("Pruned directory is ${baseDir.findDriveTarget(Int.MAX_VALUE, spaceNeeded)}")
}

class SysFile(
  name: String,
  size: Int
) {
  val size = size
  val name = name
}

class SysDir(
  name: String
) {
  val name = name
  val files = mutableListOf<SysFile>()
  val dirs = mutableMapOf<String, SysDir>()
  val maxDirCountLimit  = 100000
  val driveNeeded = 40000000

  fun addDir(dirName: String) {
    dirs.put(dirName, SysDir(dirName))
  }

  fun addFile(fileName: String, fileSize: Int) {
    files.add(SysFile(fileName, fileSize))
  }

  fun size(): Int {
    var size = 0
    files.forEach {
      size += it.size
    }
    dirs.forEach {
      size += it.value.size()
    }
  //  println("$name size is $size")
    return size
  }

  fun probSize(): Int {
    var probSize = 0
    dirs.forEach() {
      probSize += it.value.probSize()
    }
    if (size() <= maxDirCountLimit) {
      probSize += size()
    }
    return probSize
  }

  fun findDriveTarget(currSuggestion: Int, target: Int) : Int {
    var suggestion = currSuggestion
    println("suggestion is $suggestion")
    dirs.values.forEach {
      val size = it.findDriveTarget(suggestion, target)
      if (size >= target && suggestion > size) {
        suggestion = size
      }
    }
    val size = size()
    if (size >= target && suggestion > size) {
      suggestion = size
    }
    return suggestion
  }
}