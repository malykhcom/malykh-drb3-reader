package com.malykh.drb3.database.value

final case class UnitInfo(id: UnitId, defaultUnitStringId: Option[StringId], metricUnitStringId: Option[StringId],
                          metricSlope: Float, metricOffset: Float)
