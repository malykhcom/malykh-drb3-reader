package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{Location, StringId}

import scala.collection.mutable

final class StringTable(private val map: mutable.HashMap[StringId, Location] = new mutable.HashMap()) extends AnyVal {

  inline def locationById(stringId: StringId): Location = {
    map(stringId)
  }
  
  private def loadRow(rowData: RowData): Unit = {
    val id = StringId(rowData.uintFromColumn(0))
    val location = rowData.uintFromColumn(1)
    map(id) = Location(location)
  }
  
  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
