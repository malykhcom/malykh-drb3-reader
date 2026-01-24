package com.malykh.drb3.database.value

opaque type TxId = Int

object TxId {
  def apply(id: Int): TxId = id

  extension (txId: TxId) {
    def toHexString: String = Integer.toHexString(txId)
  }
}
