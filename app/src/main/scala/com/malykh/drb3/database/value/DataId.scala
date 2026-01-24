package com.malykh.drb3.database.value

opaque type DataId = Int

object DataId {
  def apply(v: Int): DataId = v
}
