package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult
import org.tindalos.principle.domain.analyzers.submodulesblueprint.Submodule
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

class PlainEnglishSubmodulesBlueprintAnalysisResultReporter extends SubmodulesBlueprintAnalysisResultReporter {

  def report(result: SubmodulesBlueprintAnalysisResult):AnalysisResultsReporter.Report = {
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("Submodules Blueprint violations (" + result.violationsNumber + " of the allowed " + result.threshold + ")")
    sb.append("\n" + sectionLine + "\n")

    if (!result.overlaps.isEmpty) {
      sb.append("Invalid blueprint definition, overlapping modules" + "\n")
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

  private def printIllegalDependencies(submodule: Submodule, dependencies: Set[Submodule]) = "Illegal dependency: " + submodule.id.value() + " -> " + dependencies.map(_.id.value())

  private def printMissingDependencies(submodule: Submodule, dependencies: Set[Submodule]) = "Missing dependency: " + submodule.id.value() + " -> " + dependencies.map(_.id.value())

}