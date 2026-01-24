package com.malykh.drb3.database.table

import com.malykh.drb3.database.loader.RowData
import com.malykh.drb3.database.value.{ServiceCategoryId, StringId}

import scala.collection.mutable

final class ServiceCategoryTable(private val map: mutable.HashMap[ServiceCategoryId, StringId] = new mutable.HashMap()) extends AnyVal{

  inline def nameById(serviceCategoryId: ServiceCategoryId): StringId = {
    map(serviceCategoryId)
  }

  private def loadRow(rowData: RowData): Unit = {
    val nameId = StringId(rowData.uintFromColumn(1))
    val serviceCatId = ServiceCategoryId(rowData.uintFromColumn(3))
    map(serviceCatId) = nameId
  }

  def rowLoader(rowCount: Int): RowData => Unit = {
    map.sizeHint(rowCount)
    loadRow
  }
}
