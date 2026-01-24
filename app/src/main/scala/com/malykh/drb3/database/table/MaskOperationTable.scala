package com.malykh.drb3.database.table

import com.malykh.drb3.database.*
import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{MaskOperation, MaskOperationId, Operator}

import scala.collection.mutable

final class MaskOperationTable(private val map: mutable.HashMap[MaskOperationId, MaskOperation] = new mutable.HashMap()) extends AnyVal {
  
  inline def maskOperationById(maskOperationId: MaskOperationId): MaskOperation = {
    map(maskOperationId)
  }
  
  private def loadRow(rowData: RowData): Unit = {
    val id = MaskOperationId(rowData.uintFromColumn(0))
    val mask = rowData.uintFromColumn(1)
    val operator = Operator(rowData.uint8FromColumn(2))

    map(id) = MaskOperation(mask, operator)
  }
  
  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
