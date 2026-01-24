package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{StringId, UnitId, UnitInfo}

import scala.collection.mutable

final class UnitTable(private val map: mutable.HashMap[UnitId, UnitInfo] = new mutable.HashMap()) extends AnyVal {

  inline def unitInfoById(unitId: UnitId): UnitInfo = {
    map(unitId)
  }

  private def loadRow(rowData: RowData): Unit = {
    val id = UnitId(rowData.uintFromColumn(0))

    inline def optString(col: Int): Option[StringId] = {
      rowData.uintFromColumn(col) match {
        case 0 => None
        case x => Some(StringId(x))
      }
    }

    val metricUnitStringId = optString(3)
    val defaultUnitStringId = optString(6)

    inline def asFloat(col: Int): Float = {
      java.lang.Float.intBitsToFloat(rowData.uintFromColumn(col))
    }

    val metricSlope = asFloat(2)
    val metricOffset = asFloat(4)

    map(id) = UnitInfo(id, defaultUnitStringId, metricUnitStringId, metricSlope, metricOffset)
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
