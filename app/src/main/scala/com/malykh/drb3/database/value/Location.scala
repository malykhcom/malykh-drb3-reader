package com.malykh.drb3.database.value

opaque type Location = Int

object Location {
  def apply(value: Int): Location = value

  extension (location: Location) {
    def textNum: Int = (location >> 24) & 0xff
    def offset: Int = location & 0xffffff
    def toHexString: String = Integer.toHexString(location)
  }
}
