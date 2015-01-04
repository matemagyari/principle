package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.agents.submodulesblueprint.SubmodulesBlueprintAnalysisResult
import org.tindalos.principle.domain.agents.submodulesblueprint.Submodule
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

import scala.collection.JavaConversions._

object SubmodulesBlueprintViolationsReporter {

  def report(result: SubmodulesBlueprintAnalysisResult):AnalysisResultsReporter.Report = {
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tSubmodules Blueprint violations (" + result.violationsNumber + " of the allowed " + result.threshold + ")\t")
    sb.append("\n" + sectionLine + "\n")

    if (!result.overlaps.isEmpty()) {
      sb.append("Invalid blueprint definition, overlapping modulules")
    } else if (result.violationsNumber == 0) {
      sb.append("No violations.\n")
    } else {
      result.illegalDependencies.foreach({ keyVal =>
        sb.append(printIllegalDependencies(keyVal._1, keyVal._2) + "\n")
      })
      result.missingDependencies.foreach({ keyVal =>
        sb.append(printMissingDependencies(keyVal._1, keyVal._2) + "\n")
      })
    }
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  private def printIllegalDependencies(submodule: Submodule, dependencies: Set[Submodule]) = "Illegal dependency: " + submodule + " -> " + dependencies

  private def printMissingDependencies(submodule: Submodule, dependencies: Set[Submodule]) = "Missing dependency: " + submodule + " -> " + dependencies

}