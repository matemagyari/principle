package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.detector.sap.SAPResult

object SAPViolationsReporter {

  def report(result: SAPResult) = {
    val outlierPackages = result.outlierPackages
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tStable Abstractions Principle violations (" + outlierPackages.length + " of allowed " + result.threshold + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (outlierPackages.isEmpty) {
      sb.append("No violations.\n")
    } else {
      outlierPackages foreach { outlierPackage =>
        sb.append(print(outlierPackage) + "\n")
      }
    }
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  private def print(outlierPackage: Package) = outlierPackage + " " + outlierPackage.distance

}