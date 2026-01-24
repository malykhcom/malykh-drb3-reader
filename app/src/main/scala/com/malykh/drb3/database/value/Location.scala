package com.malykh.drb3.database.value

opaque type Location = Int

object Location {
  def apply(value: Int): Location = value

  extension (location: Location) {
    def textNum: Int = (location >> 24) & 0xFF
    def offset: Int = location & 0xFFFFFF
    def toHexString: String = Integer.toHexString(location)
  }
}
