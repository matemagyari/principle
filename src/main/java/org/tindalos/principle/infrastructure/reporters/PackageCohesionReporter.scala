package org.tindalos.principle.infrastructure.reporters

import java.io.PrintWriter

import org.tindalos.principle.domain.detector.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object PackageCohesionReporter {

  def report(result: CohesionAnalysisResult): AnalysisResultsReporter.Report = {
    val cohesionDetailsFileName = "cohesion_details.txt"
    val printWriter = new PrintWriter(cohesionDetailsFileName)

    result.packages
      .toList
      .sortBy(_._2.cohesion())
      .foreach {
        line => printWriter.append(line._2.generalCohesion + "/" + line._2.internalCohesion + " " + line._1 + "\n")
    }
    printWriter.close()

    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tPackage Cohesion Analysis\t")
    sb.append("\n" + sectionLine + "\n")
    sb.append(s"\nFor details check file: ${cohesionDetailsFileName} \n\n")

    sb.append(sectionLine + "\n")

    sb.toString()
  }


}