package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.coredetector.ViolationsReporter
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.core.Cycle

class ADPViolationsReporter extends ViolationsReporter[ADPResult] {

  override def report(result: ADPResult) = {
    val cyclesByBreakingPoints = result.cyclesByBreakingPoints
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tAcyclic Package Dependency Principle violations (" + cyclesByBreakingPoints.size + " of the allowed "
      + result.threshold + ")\t")
    sb.append("\n" + sectionLine + "\n")

    sb.append(sectionLine + "\n")

    if (cyclesByBreakingPoints.isEmpty) {
      sb.append("No violations.\n")
    } else {
      sb.append("The cycles could be broken up refactoring the following packages: \n\n")

      cyclesByBreakingPoints.foreach(keyVal => sb.append(keyVal._1 + " (" + keyVal._2.size + ")\n"))

      cyclesByBreakingPoints.foreach({ keyVal =>
        sb.append("\nExample cycles caused by " + keyVal._1 + "\n")
        keyVal._2.toList.sortBy(_.toString).foreach(cycle => { sb.append(print(cycle) + "\n") })
      })
    }

    sb.append(sectionLine + "\n")
    sb.toString()
  }

  private def print(cycle: Cycle) = {

    val arrow = "-->"
    val sb = new StringBuffer()
    cycle.references.foreach(reference => sb.append("\n" + reference + " " + arrow))
    sb.append("\n-------------------------------")
    sb.toString()
  }

  override def getType = classOf[ADPResult]

}