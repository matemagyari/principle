package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.app.reporters.SAPAnalysisResultReporter
import org.tindalos.principle.domain.analyzers.sap.SAPResult
import org.tindalos.principle.domain.core.packages.PackageWithMetrics
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

import scala.collection.JavaConverters._

class PlainEnglishSAPAnalysisResultReporter extends SAPAnalysisResultReporter {

  def report(result: SAPResult): AnalysisResultsReporter.Report = {
    val outlierPackages = result.outlierPackages().asScala
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tStable Abstractions Principle violations (" + outlierPackages.size + " of allowed " + result.threshold() + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (outlierPackages.isEmpty) {
      sb.append("No violations.\n")
    } else {
      outlierPackages foreach { (outlierPackage: PackageWithMetrics) =>
        sb.append(print(outlierPackage) + "\n")
      }
    }
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  private def print(outlierPackage: PackageWithMetrics) = outlierPackage.reference() + " " + outlierPackage.getMetrics().distance()

}