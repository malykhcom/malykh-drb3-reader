package com.malykh.drb3

import com.malykh.drb3.database.Database
import com.malykh.drb3.database.value.ModuleId

import java.nio.file.Paths
import scala.util.{Properties, Try}

object Main {
  private val version = "2026-01-25"

  private val commandNameBase = "malykh-drb3-reader"
  private val commandName = {
    if (Properties.isWin) commandNameBase + ".cmd"
    else if (Properties.isLinux) commandNameBase + ".sh"
    else commandNameBase
  }

  def main(args: Array[String]): Unit = {
    val filePath = Paths.get("database.mem")

    println(s"malykh-drb3-reader v$version (2022-2026) by Anton Malykh, malykh.com")

    val database = Database.fromFile(filePath)
    println()

    def showHelp(): Unit = {
      println("Usage:")
      val exampleModuleId = Info.moduleIdForExample(database)
      println(s"Show txs for module (for example): $commandName $exampleModuleId")
      println(s"Show txs for all modules (very large output): $commandName all")
    }

    if (args.isEmpty) {
      Info.printAllModules(database)
      println()
      showHelp()
    }
    else {
      val arg = args.head
      val idOpt = Database.parseHexValueOpt(arg)
      idOpt match {
        case Some(id) =>
          val res = Info.printTxForModule(database, ModuleId(id))
          if (!res) {
            println(s"Unknown module id: ${Database.hexValue(id.toHexString)}")
            showHelp()
          }
        case _ if arg == "all" =>
          Info.printAllTx(database)
        case _ =>
          println("Incorrect module id: " + arg)
          showHelp()
      }
    }
  }
}
