package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.*

import scala.collection.mutable

final class ModuleTable(private val map: mutable.TreeMap[ModuleId, Module] = new mutable.TreeMap()) extends AnyVal {
  
  inline def allModulesIterator(): Iterator[Module] = {
    map.valuesIterator
  }
  
  inline def moduleByIdOpt(moduleId: ModuleId): Option[Module] = {
    map.get(moduleId)
  } 

  private def loadRow(rowData: RowData): Unit = {
    val id = ModuleId(rowData.uintFromColumn(0))
    val serviceCatId = ServiceCategoryId(rowData.uintFromColumn(1))
    val nameId = StringId(rowData.uintFromColumn(3))
    val module = Module(id, serviceCatId, nameId)
    map(id) = module
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
