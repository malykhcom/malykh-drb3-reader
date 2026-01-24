package com.malykh.drb3.util

final class Duration(val startTimeNano: Long) extends AnyVal {
  inline def durationMs(): Long = {
    (System.nanoTime() - startTimeNano) / 1000000L
  }
}

object Duration {
  inline def start(): Duration = {
    new Duration(System.nanoTime())
  }
}
