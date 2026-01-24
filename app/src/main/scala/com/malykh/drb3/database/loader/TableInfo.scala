package com.malykh.drb3.database.loader

final case class TableInfo(index: Int, tableOffset: Int, colSizes: Array[Int], rowCount: Int, rowSize: Int)
