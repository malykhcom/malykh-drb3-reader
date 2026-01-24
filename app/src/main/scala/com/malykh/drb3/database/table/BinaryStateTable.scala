package com.malykh.drb3.database.table

import com.malykh.drb3.database.*
import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{BinaryState, BinaryStateId, StringId}

import scala.collection.mutable

final class BinaryStateTable(private val map: mutable.HashMap[BinaryStateId, BinaryState] = new mutable.HashMap()) extends AnyVal {

  inline def binaryStateById(binaryStateId: BinaryStateId): BinaryState = {
    map(binaryStateId)
  }

  private def loadRow(rowData: RowData): Unit = {
    val id = BinaryStateId(rowData.uintFromColumn(0))
    val trueStringId = StringId(rowData.uintFromColumn(1))
    val falseStringId = StringId(rowData.uintFromColumn(2))

    map(id) = BinaryState(trueStringId, falseStringId)
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}

