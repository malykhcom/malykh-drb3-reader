package com.malykh.drb3.database.loader

abstract class RowData {
  def uint8FromColumn(column: Int): Int
  def uintFromColumn(column: Int): Int
  def bytesFromColumn(column: Int): Array[Byte]
  def arrayReaderFromColumn(column: Int): ArrayReader
}
