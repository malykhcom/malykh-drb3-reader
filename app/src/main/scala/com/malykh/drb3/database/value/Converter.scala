package com.malykh.drb3.database.value

import com.malykh.drb3.database.Database
import com.malykh.drb3.database.loader.ArrayReader
import com.malykh.drb3.database.value.*

import scala.annotation.switch


sealed abstract class Converter {
  def info(database: Database): String
}

final class UnknownConverter(info: String) extends Converter {
  override def info(database: Database): String = info
}

final class StateConverter(stateId: StateId, maskOperationIdOpt: Option[MaskOperationId]) extends Converter {
  override def info(database: Database): String = {
    val sb = new StringBuilder()
    sb.append("Type: State")

    if (maskOperationIdOpt.isEmpty)
      sb.append(" (Alt)")

    sb.append('\n')

    for (maskOperationId <- maskOperationIdOpt) {
      val mask = database.maskOperationTable.maskOperationById(maskOperationId).mask
      if (mask != 0)
        sb.append("Mask: 0x").append(mask.toHexString).append('\n')
    }

    locally {
      val d = database.defaultStateTable.defaultStateByIdOpt(stateId).fold("N/A")(database.string)
      sb.append("Default: ").append(d).append('\n')
    }

    for (states <- database.stateTable.statesByIdOpt(stateId)) {
      for ((value, stringId) <- states) {
        sb.append("0x").append(value.toHexString).append(": ").append(database.string(stringId)).append('\n')
      }
    }

    sb.toString()
  }
}

final class BinaryStateConverter(binaryStateId: BinaryStateId, maskOperationIdOpt: Option[MaskOperationId]) extends Converter {
  override def info(database: Database): String = {
    val sb = new StringBuilder()
    sb.append("Type: Binary State")

    if (maskOperationIdOpt.isEmpty)
      sb.append(" (Alt)")

    sb.append('\n')

    locally {
      val binaryState = database.binaryStateTable.binaryStateById(binaryStateId)
      sb.append("False: ").append(database.string(binaryState.falseStringId)).append('\n')
      sb.append("True: ").append(database.string(binaryState.trueStringId)).append('\n')
    }

    for (maskOperationId <- maskOperationIdOpt) {
      val mask = database.maskOperationTable.maskOperationById(maskOperationId)
      sb.append("Mask: 0x").append(mask.mask.toHexString).append('\n')
      sb.append("Operator: ").append(mask.operator.title).append('\n')
    }

    sb.toString()
  }
}

final class NumericConverter(unitId: UnitId, numericIdOpt: Option[NumericId]) extends Converter {
  override def info(database: Database): String = {
    val sb = new StringBuilder()
    sb.append("Type: Numeric")

    if (numericIdOpt.isEmpty)
      sb.append(" (Alt)")

    sb.append('\n')

    for (numericId <- numericIdOpt) {
      val numeric = database.numericTable.numericById(numericId)
      sb.append("Slope: ").append(numeric.slope).append('\n')
      sb.append("Offset: ").append(numeric.offset).append('\n')
    }

    val unitInfo = database.unitTable.unitInfoById(unitId)
    locally {
      def str(optString: Option[StringId]) = optString.fold("")(database.string) match {
        case "" => "(empty)"
        case x => x
      }

      val defaultUnitStringId = unitInfo.defaultUnitStringId
      val metricUnitStringId = unitInfo.metricUnitStringId
      if (defaultUnitStringId == metricUnitStringId) {
        sb.append("Unit: ").append(str(defaultUnitStringId)).append('\n')
      }
      else {
        sb.append("Default unit: ").append(str(unitInfo.defaultUnitStringId)).append('\n')
        sb.append("Metric unit: ").append(str(unitInfo.metricUnitStringId)).append('\n')
      }
    }
    locally {
      val metricSlope = unitInfo.metricSlope
      val metricOffset = unitInfo.metricOffset
      if (metricSlope != 1.0 || metricOffset != 0.0) {
        sb.append("Metric slope: ").append(metricSlope).append('\n')
        sb.append("Metric offset: ").append(metricOffset).append('\n')
      }
    }

    sb.toString()
  }
}

object Converter {
  def fromArrayReader(arrayReader: ArrayReader): Converter = {
    val converterType = arrayReader.uint8(offset = 0)

    val dsId = arrayReader.uint(offset = 2, len = 2)
    inline def cfId = arrayReader.uint(offset = 4, len = 2)

    (converterType: @switch) match {
      case 0x00 =>
        val binaryStateId = BinaryStateId(dsId)
        val maskOperationId = MaskOperationId(cfId)
        new BinaryStateConverter(binaryStateId, Some(maskOperationId))
      case 0x02 =>
        val binaryStateId = BinaryStateId(dsId)
        new BinaryStateConverter(binaryStateId, None)
      case 0x11 =>
        val unitId = UnitId(dsId)
        val numericId = NumericId(cfId)
        new NumericConverter(unitId, Some(numericId))
      case 0x12 =>
        val unitId = UnitId(dsId)
        new NumericConverter(unitId, None)
      case 0x20 =>
        val stateId = StateId(dsId)
        val maskOperationId = MaskOperationId(cfId)
        new StateConverter(stateId, Some(maskOperationId))
      case 0x22 =>
        val stateId = StateId(dsId)
        new StateConverter(stateId, None)
      case _ =>
        val info = s"Type: 0x${converterType.toHexString} (0x${dsId.toHexString}, 0x${cfId.toHexString})\n"
        new UnknownConverter(info)
    }
  }
}
