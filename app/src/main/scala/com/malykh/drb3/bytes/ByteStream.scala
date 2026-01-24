package com.malykh.drb3.bytes

final class ByteStream(memoryBytes: MemoryBytes, private var offset: Int) {
  def bytes(len: Int): Array[Byte] = {
    val ret = memoryBytes.bytes(offset, len)
    offset += len
    ret
  }

  def int32(): Int = {
    val ret = memoryBytes.int32(offset)
    offset += 4
    ret
  }

  def uint16(): Int = {
    val ret = memoryBytes.uint16(offset)
    offset += 2
    ret
  }

  def uint8(): Int = {
    val ret = memoryBytes.uint8(offset)
    offset += 1
    ret
  }
}

