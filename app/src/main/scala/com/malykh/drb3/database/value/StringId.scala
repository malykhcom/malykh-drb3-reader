package com.malykh.drb3.database.value

opaque type StringId = Int

object StringId {
  def apply(id: Int): StringId = id
}
