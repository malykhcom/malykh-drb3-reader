package com.malykh.drb3.database.value

opaque type NumericId = Int

object NumericId {
  def apply(id: Int): NumericId = id
}
