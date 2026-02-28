package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

import scala.collection.JavaConverters._

class PlainEnglishThirdPartyAnalysisResultReporter extends ThirdPartyAnalysisResultReporter {

  def report(result: ThirdPartyViolationsResult): AnalysisResultsReporter.Report = {
    val violations = result.violations.asScala
    val violationCount = violations.values.map(_.size).sum
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\nThird party violations (" + violationCount + " of allowed " + result.threshold() + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (violations.isEmpty) sb.append("No violations.\n")
    else violations.toSeq.sortBy(_._1.toString).foreach { case (referrer, deps) =>
      deps.asScala.toSeq.sorted.foreach(dep => sb.append(s"$referrer refers to $dep\n"))
    }

    sb.append(sectionLine + "\n")
    sb.toString()
  }

}