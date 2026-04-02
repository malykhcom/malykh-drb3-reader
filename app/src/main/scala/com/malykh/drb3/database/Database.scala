package com.malykh.drb3.database

import com.malykh.drb3.database.loader.DatabaseLoader
import com.malykh.drb3.database.table.*
import com.malykh.drb3.database.value.StringId

import java.nio.file.Path
import scala.util.Try

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
  private inline def hexPrefix = "0x"
  inline def hexValue(rawHexString: String): String = hexPrefix + rawHexString
  def parseHexValueOpt(hexValue: String): Option[Int] = {
    if (hexValue.startsWith(Database.hexPrefix)) {
      Try {
        Integer.parseInt(hexValue.substring(Database.hexPrefix.length), 16)
      }.toOption
    }
    else {
      None
    }
  }
  def fromFile(filePath: Path): Database = {
    DatabaseLoader.loadDatabase(filePath)
  }
}
