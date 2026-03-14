package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.tindalos.principle.app.reporters.AnalysisResultReporter
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager
import scala.collection.JavaConverters._
import PackageCohesionConstants._

object PackageCohesionAnalysisResultReporter {

  val columns = "| Cohesion | Size | upstream/downstream dependencies | internal/external edges |\n"

  val generalDescription = GRAPH_DESCRIPTION +
    " Cohesion between a group of vertices (classes) is calculated by the " +
    "\n\n\tC = 1 - E1 / E2 " +
    "\n\nformula. E1 is the number of edges the vertices in the group participate in. This means 'internal' edges, where both ends of the edge is from the group and 'external' ones, where only one end is. " +
    " E2 would be the number of edges belonging to the new vertex if the vertices in the group collapsed into one. So all internal edges would disappear and multiple external edges might collapse into each other as well. " +
    "\nThe cohesion measures how much relative decrease in the number of edges would a grouping of a given set of vertices cause. 0.0 means the collapsing wouldn't decrease the number of edges at all, while 1 means would be no edge left."

  def round(d: Double) = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
}

trait PackageCohesionAnalysisResultReporter extends AnalysisResultReporter[CohesionAnalysisResult]

class PlainEnglishPackageCohesionAnalysisResultReporter extends PackageCohesionAnalysisResultReporter {

  def report(result: CohesionAnalysisResult): AnalysisResultsReporter.Report = {

    var fileNames = s"${PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME}, ${PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME}, ${PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME}"

    ExistingPackageCohesionsFileWriter.writeToFile(result)
    PackageStructureHints1FileWriter.writeToFile(result.groupingResult)

    PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition)
    if (result.cohesiveNodeGroups().isPresent) {
      CohesiveGroupsFileWriter.writeToFile(result.cohesiveNodeGroups().get().asScala.toSet)
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