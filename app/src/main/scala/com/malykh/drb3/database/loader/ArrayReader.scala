package com.malykh.drb3.database.loader

abstract class ArrayReader {
  def uint8(offset: Int): Int
  def uint(offset: Int, len: Int): Int
}
