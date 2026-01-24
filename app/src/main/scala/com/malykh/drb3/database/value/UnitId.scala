package com.malykh.drb3.database.value

opaque type UnitId = Int

object UnitId {
  def apply(id: Int): UnitId = id
}
