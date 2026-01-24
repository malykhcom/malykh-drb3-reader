package com.malykh.drb3.bytes

import java.nio.file.{Files, Path}

final class MemoryBytes(val bytes: Array[Byte]) extends AnyVal {
  def size: Int = bytes.length

  def bytes(offset: Int, len: Int): Array[Byte] = {
    val ret = new Array[Byte](len)
    Array.copy(bytes, offset, ret, 0, len)
    ret
  }

  inline def int32(offset: Int): Int = {
    java.lang.Byte.toUnsignedInt(bytes(offset)) +
      (java.lang.Byte.toUnsignedInt(bytes(offset+1)) << 8) +
      (java.lang.Byte.toUnsignedInt(bytes(offset+2)) << 16) +
      (java.lang.Byte.toUnsignedInt(bytes(offset+3)) << 24)
  }

  inline def uint16(offset: Int): Int = {
    java.lang.Byte.toUnsignedInt(bytes(offset)) +
      (java.lang.Byte.toUnsignedInt(bytes(offset + 1)) << 8)
  }

  inline def uint8(offset: Int): Int = {
    java.lang.Byte.toUnsignedInt(bytes(offset))
  }

  def uintNetworkOrder(offset: Int, len: Int): Int = {
    var ret = 0
    for (i <- 0 until len) {
      val b = bytes(offset - i - 1 + len)
      ret += java.lang.Byte.toUnsignedInt(b) << (i * 8)
    }
    ret
  }

  def uintIntelOrder(offset: Int, len: Int): Int = {
    var ret = 0
    for (i <- 0 until len) {
      val b = bytes(offset + i)
      ret += java.lang.Byte.toUnsignedInt(b) << (i * 8)
    }
    ret
  }
}

object MemoryBytes {
  def fromFile(filePath: Path): MemoryBytes = {
    val bytes = Files.readAllBytes(filePath)
    new MemoryBytes(bytes)
  }
}
