package com.malykh.drb3.database.loader

import com.malykh.drb3.bytes.{ByteStream, MemoryBytes}
import com.malykh.drb3.database.Database
import com.malykh.drb3.util.Duration

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import scala.annotation.switch

object DatabaseLoader {
  private val starScanMark = "StarSCAN".getBytes(StandardCharsets.US_ASCII)

  private def loadTableInfos(memoryBytes: MemoryBytes): Array[TableInfo] = {
    val byteStream = new ByteStream(memoryBytes, offset = 0)

    val size = byteStream.int32() // unused
    val id = byteStream.uint16() // unused

    val tableNum = byteStream.uint16()
    val tableInfos = new Array[TableInfo](tableNum)

    for (i <- 0 until tableNum) {
      val tableOffset = byteStream.int32()
      val rowCount = byteStream.uint16()
      val rowSize = byteStream.uint16()

      val colCountStated = byteStream.uint8()
      val colsBytes = byteStream.bytes(27)

      if (colCountStated > colsBytes.length)
        sys.error("Too many columns stated: " + colCountStated + " of " + colsBytes.length)

      def fromByte(b: Byte): Int = java.lang.Byte.toUnsignedInt(b)

      def nonEmpty(i: Int): Boolean = i != 0

      val colSizes = colsBytes.take(colCountStated).map(fromByte).filter(nonEmpty)

      tableInfos(i) = TableInfo(i, tableOffset, colSizes, rowCount, rowSize)
    }

    tableInfos
  }

  private def loadRows(isStarScan: Boolean, tableInfo: TableInfo, tableStream: ByteStream, rowFunc: RowData => Unit): Unit = {
    val colSizes = tableInfo.colSizes

    def sum(a: Int, b: Int) = a + b

    val colOffsets = colSizes.scanLeft(0)(sum)

    for (_ <- 0 until tableInfo.rowCount) {
      val row = tableStream.bytes(tableInfo.rowSize)
      val rowBytes = new MemoryBytes(row)

      inline def bytes(column: Int): Array[Byte] = rowBytes.bytes(colOffsets(column), colSizes(column))

      val rowData = new RowData {
        override def uint8FromColumn(column: Int): Int = {
          rowBytes.uint8(colOffsets(column))
        }

        override def uintFromColumn(column: Int): Int = {
          val offset = colOffsets(column)
          val size = colSizes(column)
          if (isStarScan)
            rowBytes.uintIntelOrder(offset, size)
          else
            rowBytes.uintNetworkOrder(offset, size)
        }

        override def bytesFromColumn(column: Int): Array[Byte] = bytes(column)

        override def arrayReaderFromColumn(column: Int): ArrayReader = {
          val arrayBytes = new MemoryBytes(bytes(column))

          if (isStarScan) {
            new ArrayReader {
              override def uint8(offset: Int): Int = arrayBytes.uint8(offset)

              override def uint(offset: Int, len: Int): Int = arrayBytes.uintIntelOrder(offset, len)
            }
          } else {
            new ArrayReader {
              override def uint8(offset: Int): Int = arrayBytes.uint8(offset)

              override def uint(offset: Int, len: Int): Int = arrayBytes.uintNetworkOrder(offset, len)
            }
          }
        }
      }
      rowFunc(rowData)
    }
  }


  private def loadTable(database: Database, memoryBytes: MemoryBytes, tableInfo: TableInfo): Unit = {
    val tableStream = new ByteStream(memoryBytes, offset = tableInfo.tableOffset)
    val rowCount = tableInfo.rowCount

    inline def load(rowFunc: RowData => Unit): Unit = {
      loadRows(database.isStarScan, tableInfo, tableStream, rowFunc)
    }

    (tableInfo.index: @switch) match {
      case 0 => load(database.moduleTable.rowLoader(rowCount))
      //case 1 => // TABLE_DES_INFO // DESRecord
      case 2 => load(database.binaryStateTable.rowLoader(rowCount))
      case 4 => load(database.maskOperationTable.rowLoader(rowCount))
      case 5 => load(database.numericTable.rowLoader(rowCount))
      case 6 => load(database.serviceCategoryTable.rowLoader(rowCount))
      //case 7 => // TABLE_QUALIFIER
      case 8 => load(database.dataTable.rowLoader(rowCount))
      //case 9 => // TABLE_DRB_MENU // MenuRecord
      case 10 => load(database.moduleTxTable.rowLoader())
      case 13 => load(database.defaultStateTable.rowLoader(rowCount))
      case 15 => load(database.stateTable.rowLoader())
      case 16 => load(database.stringTable.rowLoader(rowCount))
      case 17 => load(database.unitTable.rowLoader(rowCount))
      //case 18 => // TABLE_DATAELEMENT_QUALIFIER
      //
      case 23 => load(database.txTable.rowLoader(rowCount))
      //
      case 26 => database.textTable.load0(tableStream, rowCount, tableInfo.rowSize)
      case 27 => database.textTable.load1(tableStream, rowCount, tableInfo.rowSize)
      case _ => //skip
    }
  }

  def loadDatabase(filePath: Path): Database = {
    println("File path: " + filePath.toRealPath())

    val t = Duration.start()

    val memoryBytes = MemoryBytes.fromFile(filePath)

    val isStarScan = locally {
      val offset = memoryBytes.size - 0x17
      val starScanBytes = memoryBytes.bytes(offset, 8)
      starScanBytes.sameElements(starScanMark)
    }

    println("StarScan DB: " + isStarScan)

    val tableInfos = loadTableInfos(memoryBytes)
    
    val database = new Database(isStarScan)

    for (tableInfo <- tableInfos) {
      loadTable(database, memoryBytes, tableInfo)
    }

    println(s"Loading time: ${t.durationMs()}ms")
    database
  }
}
