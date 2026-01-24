package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{StateId, StringId}

import scala.collection.mutable

final class StateTable(private val map: mutable.HashMap[StateId, mutable.TreeMap[Int, StringId]] = new mutable.HashMap()) extends AnyVal {

  inline def statesByIdOpt(stateId: StateId): Option[Iterable[(Int, StringId)]] = {
    map.get(stateId)
  }
  
  private def loadRow(rowData: RowData): Unit = {
    val stringId = StringId(rowData.uintFromColumn(0))
    val value = rowData.uintFromColumn(1)
    val stateId = StateId(rowData.uintFromColumn(3))

    val valueMap = map.getOrElseUpdate(stateId, new mutable.TreeMap())
    valueMap(value) = stringId
  }

  def rowLoader(): RowData => Unit = {
    loadRow
  }
}
