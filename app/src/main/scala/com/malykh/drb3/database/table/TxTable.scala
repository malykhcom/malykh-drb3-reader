package com.malykh.drb3.database.table

import com.malykh.drb3.bytes.ByteHelper
import com.malykh.drb3.database.*
import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{Converter, DataId, ServiceCategoryId, StringId, Tx, TxId}

import scala.collection.mutable

final class TxTable(private val map: mutable.HashMap[TxId, Tx] = new mutable.HashMap()) extends AnyVal {

  inline def txById(txId: TxId): Tx = {
    map(txId)
  }
  
  private def loadRow(rowData: RowData): Unit = {
    val id = TxId(rowData.uintFromColumn(0))
    val converter = Converter.fromArrayReader(rowData.arrayReaderFromColumn(1))
    val dataId = DataId(rowData.uintFromColumn(2))
    val bytesRaw = rowData.bytesFromColumn(6)
    val nameId = StringId(rowData.uintFromColumn(8))
    val serviceCategoryId = ServiceCategoryId(rowData.uintFromColumn(14) >> 8)
    val bytes = new Array[Byte](bytesRaw(0))
    Array.copy(src = bytesRaw, srcPos = 1, dest = bytes, destPos = 0, length = bytes.length)
    map(id) = Tx(id, converter, dataId, bytes, nameId, serviceCategoryId)
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
