package com.malykh.drb3

import com.malykh.drb3.bytes.ByteHelper
import com.malykh.drb3.database.value.Protocol
import com.malykh.drb3.database.value.*
import com.malykh.drb3.database.Database

import scala.util.Random

object Info {
  def moduleIdForExample(database: Database): String = {
    val modules = database.moduleTable.allModulesIterator().toArray
    val hexModuleString = if (modules.isEmpty) {
      "5d6"
    }
    else {
      modules(Random.nextInt(modules.length)).id.toHexString
    }
    Database.hexValue(hexModuleString)
  }
  private def printModule(database: Database, module: Module): Unit = {
    val name = database.string(module.nameId)
    val category = database.string(database.serviceCategoryTable.nameById(module.serviceCategoryId))
    println(s"Module ${Database.hexValue(module.id.toHexString)}: $name [$category]")
  }
  def printAllModules(database: Database): Unit = {
    for (m <- database.moduleTable.allModulesIterator()) {
      printModule(database, m)
    }
  }
  def printTxForModule(database: Database, moduleId: ModuleId): Boolean = {
    database.moduleTable.moduleByIdOpt(moduleId) match {
      case Some(module) =>
        printModule(database, module)
        println()
        val txs = database.moduleTxTable.moduleTxsByIdOpt(moduleId).getOrElse(sys.error(s"Unknown module id: ${Database.hexValue(moduleId.toHexString)}"))
        for (txId <- txs) {
          val tx = database.txTable.txById(txId)
          val name = database.string(tx.nameId)
          val serviceCategory = database.string(database.serviceCategoryTable.nameById(tx.serviceCategoryId))
          val data = database.dataTable.dataById(tx.dataId)
          val protocol = data.protocol.title
          println(s"Tx ${Database.hexValue(txId.toHexString)}: $name [$serviceCategory], $protocol: ${ByteHelper.toString(tx.bytes)}")
          println(tx.converter.info(database))
        }
        true
      case _ => 
        false
    }
  }
  def printAllTx(database: Database): Unit = {
    for (m <- database.moduleTable.allModulesIterator()) {
      println()
      printTxForModule(database, m.id)
    }
  }
}
