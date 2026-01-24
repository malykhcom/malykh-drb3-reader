package com.malykh.drb3.database.value

opaque type StateId = Int

object StateId {
  given ordering: Ordering[StateId] with {
    override def compare(x: StateId, y: StateId): Int = x.compare(y)
  }
  def apply(v: Int): StateId = v

  extension (stateId: StateId) {
    def toHexString: String = Integer.toHexString(stateId)
  }
}
