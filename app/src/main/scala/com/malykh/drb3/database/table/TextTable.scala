package com.malykh.drb3.database.table

import com.malykh.drb3.bytes.ByteStream
import com.malykh.drb3.database.Database
import com.malykh.drb3.database.value.Location

import java.nio.charset.StandardCharsets

final class TextTable(private val texts: Array[String] = new Array(2)) extends AnyVal {

  private def load(textNum: Int, byteStream: ByteStream, rowCount: Int, rowSize: Int): Unit = {
    val sb = new StringBuilder(rowSize * rowCount)
    for (r <- 0 until rowCount) {
      val row = byteStream.bytes(rowSize)
      val string = new String(row, StandardCharsets.ISO_8859_1)
      sb.append(string)
    }
    texts(textNum) = sb.toString()
  }

  inline def load0(byteStream: ByteStream, rowCount: Int, rowSize: Int): Unit = {
    load(0, byteStream, rowCount, rowSize)
  }

  inline def load1(byteStream: ByteStream, rowCount: Int, rowSize: Int): Unit = {
    load(1, byteStream, rowCount, rowSize)
  }

  def stringByLocation(location: Location): String = {
    val textNum = location.textNum
    val start = location.offset

    def unknown() = "?" + Database.hexValue(location.toHexString) + "?"

    if (textNum < texts.length) {
      val text = texts(textNum)
      if (start < text.length) {
        // NUL-terminated string
        text.indexOf('\u0000', start) match {
          case -1 => unknown()
          case end => text.substring(start, end)
        }
      }
      else {
        unknown()
      }
    }
    else {
      unknown()
    }
  }
}
