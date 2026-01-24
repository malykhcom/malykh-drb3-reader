package com.malykh.drb3.database.value

opaque type ServiceCategoryId = Int

object ServiceCategoryId {
  def apply(id: Int): ServiceCategoryId = id
}
