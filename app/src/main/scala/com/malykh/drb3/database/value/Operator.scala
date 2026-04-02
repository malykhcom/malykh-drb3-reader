package com.malykh.drb3.database.value

import com.malykh.drb3.database.Database

import scala.annotation.switch

opaque type Operator = Int

object Operator {
  def apply(value: Int): Operator = value

  extension (operator: Operator) {
    def title: String = {
      (operator: @switch) match {
        case 0x21 => "NOT_EQUAL"
        case 0x30 => "MASK_ZERO"
        case 0x39 => "MASK_NOT_ZERO"
        case 0x3c => "LESS"
        case 0x3d => "EQUAL"
        case 0x3e => "GREATER"
        case x => s"UNKNOWN(${Database.hexValue(operator.toHexString)})"
      }
    }
  }
}
