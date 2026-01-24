package com.malykh.drb3.bytes

object ByteHelper {
  def toString(byte: Byte): String = {
    f"$byte%02x"
  }
  def toString(bytes: Array[Byte]): String = {
    bytes.map(toString).mkString(" ")
  }
}
