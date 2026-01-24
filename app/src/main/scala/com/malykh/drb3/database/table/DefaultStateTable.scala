package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{StateId, StringId}

import scala.collection.mutable

final class DefaultStateTable(private val map: mutable.HashMap[StateId, StringId] = new mutable.HashMap()) extends AnyVal {
  
  inline def defaultStateByIdOpt(stateId: StateId): Option[StringId] = {
    map.get(stateId)
  }
  
  private def loadRow(rowData: RowData): Unit = {
    rowData.uintFromColumn(1) match {
      case 0 => 
      case v =>
        val id = StateId(rowData.uintFromColumn(0))
        map(id) = StringId(v)
    }
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
