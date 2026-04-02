package com.malykh.drb3.database.value

import com.malykh.drb3.database.Database

import scala.annotation.switch

opaque type Protocol = Int

object Protocol {
  def apply(value: Int): Protocol = value
  
  extension (protocol: Protocol) {
    def title: String = {
      (protocol: @switch) match {
        case 1 => "J1850"
        case 53 => "CCD"
        case 60 => "SCI"
        case 103 => "ISO"
        case 155 => "KWP"
        case 159 => "Multimeter"
        case 160 => "J2190?"
        case _ => s"UNKNOWN(${Database.hexValue(protocol.toHexString)})"
      }
    }
  }
}
