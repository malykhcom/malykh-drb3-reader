package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.*

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

final class ModuleTxTable(private val map: mutable.HashMap[ModuleId, mutable.Buffer[TxId]] = new mutable.HashMap()) extends AnyVal {

  inline def moduleTxsByIdOpt(moduleId: ModuleId): Option[Iterable[TxId]] = {
    map.get(moduleId)
  }

  private def loadRow(rowData: RowData): Unit = {
    val moduleId = ModuleId(rowData.uintFromColumn(0))
    val txId = TxId(rowData.uintFromColumn(1))

    map.getOrElseUpdate(moduleId, new ArrayBuffer()) += txId
  }

  def rowLoader(): RowData => Unit = {
    loadRow
  }
}

