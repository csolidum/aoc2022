import java.io.File
import java.util.Optional

fun main(args: Array<String>) {
  val testFile = "./src/main/resources/day13/test.txt"
  val realFile = "./src/main/resources/day13/input.txt"
  val packetPairs = mutableListOf<Pair<ElfPacket, ElfPacket>>()
  val packetList = mutableListOf<ElfPacket>()
  var firstPacket: Optional<ElfPacket> = Optional.empty()
  var secondPacket: Optional<ElfPacket> = Optional.empty()
  File(realFile).forEachLine {
    if (it.length == 0) {
      packetPairs.add(Pair(firstPacket.get(), secondPacket.get()))
      firstPacket = Optional.empty()
      secondPacket = Optional.empty()
      return@forEachLine
    }
    println(it)
    val packet = ElfPacket.parseInput(it)
    packetList.add(packet)
    if (firstPacket.isEmpty) {
      firstPacket = Optional.of(packet)
    } else {
      secondPacket = Optional.of(packet)
    }
    println("packet size is ${packet.data.size}")
  }
  var pairIndex = 0
  var correctVal = 0
  packetPairs.forEach {
    pairIndex += 1
    println("${it.first} vs ${it.second}")
    val compare = it.first.compare(it.second)
    println("comparison $compare")
    if (compare < 0) {
      correctVal += pairIndex
    }
  }
  println(correctVal)

  val dividerPacket2 = ElfPacket.parseInput("[[2]]")
  val dividerPacket6 = ElfPacket.parseInput("[[6]]")
  packetList.add(dividerPacket2)
  packetList.add(dividerPacket6)
  packetList.sortWith(ElfPacket.ElfPacketComparator())
  println(packetList)
  var index2 = 0
  var index6 = 0
  for (i in 0 until packetList.size) {
    if (packetList[i].equals(dividerPacket2)) {
      index2 = i + 1
    }
    if (packetList[i].equals(dividerPacket6)) {
      index6 = i + 1
    }
  }

  println("$index2 $index6")
}

class ElfPacket(
  val data: List<PacketOrInt>
) {

  fun subPacket(start: Int): ElfPacket {
    return ElfPacket(data.subList(start, data.size))
  }

  override fun toString(): String {
    val dataStr = data.joinToString {
      if (it.num.isPresent) {
        it.num.get().toString()
      } else {
        "[${it.packet.get().toString()}]"
      }
    }
    return "[$dataStr]"
  }

  fun size() = data.size
  fun equals(other: ElfPacket): Boolean {
    if (size()  != other.size()) {
      return false
    }
    if (size() == 0) {
      return true
    }
    if (data[0].num.isPresent.xor(other.data[0].num.isPresent)) {
      return false
    }
    if (data[0].num.isPresent && data[0].num.get() != other.data[0].num.get()) {
      return false
    }
    if (data[0].packet.isPresent && !data[0].packet.get().equals(other.data[0].packet.get())) {
      return false
    }
    return subPacket(1).equals(other.subPacket(1))
  }

  fun compare(other: ElfPacket): Int {
    println("Comparing $this to $other")
    if (data.size == 0 && other.data.size == 0) {
      return 0
    } else if (data.size == 0 ) {
      return -1
    } else if (other.data.size == 0) {
      return 1
    }
    var firstVal = data[0]
    var secondVal = other.data[0]
    if (firstVal.num.isPresent && secondVal.num.isPresent) {
      if (firstVal.num.get() < secondVal.num.get()) {
        return -1
      } else if (secondVal.num.get() < firstVal.num.get()) {
        return 1
      }
    } else {
      if (firstVal.num.isPresent) {
        firstVal = PacketOrInt(packet = Optional.of(ElfPacket(listOf(PacketOrInt(num = Optional.of(firstVal.num.get()))))))
      }
      if (secondVal.num.isPresent) {
        secondVal = PacketOrInt(packet = Optional.of(ElfPacket(listOf(PacketOrInt(num = Optional.of(secondVal.num.get()))))))
      }
      val listComp = firstVal.packet.get().compare(secondVal.packet.get())
      if (listComp != 0) {
        return listComp
      }
    }
    return subPacket(1).compare(other.subPacket(1))
  }

  companion object {
    fun parseInput(input: String): ElfPacket {
      val data = mutableListOf<PacketOrInt>()
      var inList = false
      var listDepth = 0
      var sectionStart = 1
      for (i in 1 until input.length - 1) {
        when (input[i]) {
          '[' -> {
            inList = true
            listDepth += 1
          }
          ']' -> {
            listDepth -= 1
          }
          ',' -> {
            if (listDepth == 0) {
              if (inList) {
                data.add(PacketOrInt(packet = Optional.of(ElfPacket.parseInput(input.substring(sectionStart, i)))))
              } else {
                data.add(PacketOrInt(num = Optional.of(input.substring(sectionStart, i).toInt())))
              }
              inList = false
              sectionStart = i + 1
            }
          }
        }
      }
      if (input.length > 2) {
        if (inList) {
          data.add(
            PacketOrInt(
              packet = Optional.of(
                ElfPacket.parseInput(
                  input.substring(
                    sectionStart,
                    input.length - 1
                  )
                )
              )
            )
          )
        } else {
          data.add(
            PacketOrInt(
              num = Optional.of(
                input.substring(sectionStart, input.length - 1).toInt()
              )
            )
          )
        }
      }
      return ElfPacket(data)
    }
  }

  class PacketOrInt(
    val packet: Optional<ElfPacket> = Optional.empty(),
    val num: Optional<Int> = Optional.empty()
  ) {
    override fun toString(): String {
      if (packet.isPresent) {
        return packet.get().toString()
      } else {
        return num.get().toString()
      }
    }
  }

  class ElfPacketComparator: Comparator<ElfPacket> {
    override fun compare(first: ElfPacket, second: ElfPacket): Int {
      return first.compare(second)
    }
  }
}

