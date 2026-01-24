package com.malykh.drb3.database

import com.malykh.drb3.database.loader.DatabaseLoader
import com.malykh.drb3.database.table.*
import com.malykh.drb3.database.value.StringId

import java.nio.file.Path

final class Database(val isStarScan: Boolean) {
  val moduleTable = new ModuleTable()
  val binaryStateTable = new BinaryStateTable()
  val maskOperationTable = new MaskOperationTable()
  val numericTable = new NumericTable()
  val serviceCategoryTable = new ServiceCategoryTable()
  val dataTable = new DataTable()
  val moduleTxTable = new ModuleTxTable()
  val defaultStateTable = new DefaultStateTable()
  val stateTable = new StateTable()
  val stringTable = new StringTable()
  val unitTable = new UnitTable()
  val txTable = new TxTable()
  val textTable = new TextTable()

  def string(stringId: StringId): String = {
    textTable.stringByLocation(stringTable.locationById(stringId))
  }
}

object Database {
  def fromFile(filePath: Path): Database = {
    DatabaseLoader.loadDatabase(filePath)
  }
}
