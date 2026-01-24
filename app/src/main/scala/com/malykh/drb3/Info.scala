package com.malykh.drb3

import com.malykh.drb3.bytes.ByteHelper
import com.malykh.drb3.database.value.Protocol
import com.malykh.drb3.database.value.*
import com.malykh.drb3.database.Database

import scala.util.Random

object Info {
  def moduleIdForExample(database: Database): String = {
    val modules = database.moduleTable.allModules().toArray
    if (modules.isEmpty) {
      "0x5d6"
    }
    else {
      s"0x${modules(Random.nextInt(modules.length)).id.toHexString}"
    }
  }
  private def printModule(database: Database, module: Module): Unit = {
    val name = database.string(module.nameId)
    val category = database.string(database.serviceCategoryTable.nameById(module.serviceCategoryId))
    println(s"Module 0x${module.id.toHexString}: $name [$category]")
  }
  def printAllModules(database: Database): Unit = {
    for (m <- database.moduleTable.allModules()) {
      printModule(database, m)
    }
  }
  def printTxForModule(database: Database, moduleId: ModuleId): Boolean = {
    database.moduleTable.moduleByIdOpt(moduleId) match {
      case Some(module) =>
        printModule(database, module)
        println()
        val txs = database.moduleTxTable.moduleTxsByIdOpt(moduleId).getOrElse(sys.error(s"Unknown module id: 0x${moduleId.toHexString}"))
        for (txId <- txs) {
          val tx = database.txTable.txById(txId)
          val name = database.string(tx.nameId)
          val serviceCategory = database.string(database.serviceCategoryTable.nameById(tx.serviceCategoryId))
          val data = database.dataTable.dataById(tx.dataId)
          val protocol = data.protocol.title
          println(s"Tx 0x${txId.toHexString}: $name [$serviceCategory], $protocol: ${ByteHelper.toString(tx.bytes)}")
          println(tx.converter.info(database))
        }
        true
      case _ => 
        false
    }
  }
  def printAllTx(database: Database): Unit = {
    for (m <- database.moduleTable.allModules()) {
      println()
      printTxForModule(database, m.id)
    }
  }
}
