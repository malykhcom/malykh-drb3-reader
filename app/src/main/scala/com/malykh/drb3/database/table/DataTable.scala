package com.malykh.drb3.database.table

import com.malykh.drb3.database.*
import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{Data, DataId, Protocol}

import scala.collection.mutable

final class DataTable(private val map: mutable.HashMap[DataId, Data] = new mutable.HashMap()) extends AnyVal {

  inline def dataById(dataId: DataId): Data = {
    map(dataId)
  }

  private def loadRow(rowData: RowData): Unit = {
    val id = DataId(rowData.uintFromColumn(0))
    val requestLen = rowData.uintFromColumn(1)
    val responseLen = rowData.uintFromColumn(3)
    val protocol = Protocol(rowData.uintFromColumn(10))

    map(id) = Data(id, protocol, requestLen, responseLen)
  }

  def rowLoader(rowCounter: Int): RowData => Unit = {
    map.sizeHint(rowCounter)
    loadRow
  }
}
