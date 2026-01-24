package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{NumericId, NumericInfo}

import scala.collection.mutable

final class NumericTable(private val map: mutable.HashMap[NumericId, NumericInfo] = new mutable.HashMap()) extends AnyVal {
  
  inline def numericById(numericId: NumericId): NumericInfo = {
    map(numericId)
  }

  private def loadRow(rowData: RowData): Unit = {
    val id = NumericId(rowData.uintFromColumn(0))

    inline def asFloat(col: Int): Float = {
      java.lang.Float.intBitsToFloat(rowData.uintFromColumn(col))
    }
    val slope = asFloat(1)
    val offset = asFloat(2)

    map(id) = NumericInfo(slope, offset)
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
