package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.tindalos.principle.app.reporters.AnalysisResultReporter
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager
import PackageCohesionConstants._

trait PackageCohesionAnalysisResultReporter extends AnalysisResultReporter[CohesionAnalysisResult]

class PlainEnglishPackageCohesionAnalysisResultReporter extends PackageCohesionAnalysisResultReporter {

  def report(result: CohesionAnalysisResult): AnalysisResultsReporter.Report = {

    var fileNames = s"${PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME}, ${PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME}, ${PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME}"

    ExistingPackageCohesionsFileWriter.writeToFile(result)
    PackageStructureHints1FileWriter.writeToFile(result.groupingResult)

    PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition)
    if (result.cohesiveNodeGroups().isPresent) {
      CohesiveGroupsFileWriter.writeToFile(result.cohesiveNodeGroups().get())
      fileNames += s", ${PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME}"
    }

    val sb = new StringBuffer("\n" + PackageCohesionConstants.SECTION_LINE + "\n")
    sb.append("\tPackage Cohesion Analysis\t")
    sb.append("\n" + PackageCohesionConstants.SECTION_LINE + "\n")
    sb.append(s"\nFor details check files: ${fileNames} in ${ReportsDirectoryManager.ensureReportsDirectoryExists()}\n\n")

    sb.append(PackageCohesionConstants.SECTION_LINE + "\n")

    sb.toString()
  }

}