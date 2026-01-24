package com.malykh.drb3.database.value

opaque type ModuleId = Int

object ModuleId {
  given ordering: Ordering[ModuleId] with {
    override def compare(x: ModuleId, y: ModuleId): Int = x.compare(y)
  }
  def apply(id: Int): ModuleId = id

  extension (moduleId: ModuleId) {
    def toHexString: String = {
      Integer.toHexString(moduleId)
    }
  }

}
