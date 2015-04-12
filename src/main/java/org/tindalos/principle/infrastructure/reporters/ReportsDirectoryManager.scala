package org.tindalos.principle.infrastructure.reporters

import java.io.File

object ReportsDirectoryManager {

  val reportDirectoryPath = "./principle_reports"

  def ensureReportsDirectoryExists() {
    val reportDirectory = new File(reportDirectoryPath)
    if (!reportDirectory.exists()) reportDirectory.mkdirs()
  }

}
