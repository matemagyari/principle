package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.File

import org.tindalos.principle.domain.detector.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object PackageCohesionReporter {

  val sectionLine = "================================================================================"
  val subSectionLine = "-----------------------------------------------------------------------------"
  val columns = "| Cohesion | Size | upstream/downstream dependencies | internal/external edges |\n"
  val reportDirectory = "./reports"
  val cohesiveGroupsFileName = "cohesive_groups.txt"
  val packageCohesionsFileName = "existing_packages_cohesion.txt"
  val verticalGroupingFileName = "vertical_grouping.txt"

  val graphDescription = "A directed graph is built representing the structure of the code, " +
    "where each class appears as a vertex and each relationship between classes (composition, inheritance, ...) " +
    "as a directed edge between the corresponding two vertices."

  val generalDescription = graphDescription +
    "Cohesion between a group of vertices (classes) is calculated by the C = 1 - E1 / E2 formula. " +
    "\n\t-- E1 is the number of edges the vertices in the group participate in. This means 'internal' edges, where both ends of the edge is from the group and 'external' ones, where only one end is. " +
    "\n\t-- E2 would be the number of edges belonging to the new vertex if the vertices in the group collapsed into one. So all internal edges would disappear and multiple external edges might collapse into each other as well. " +
    "\nThe cohesion measures how much relative decrease in the number of edges would a grouping of a given set of vertices cause. 0.0 means the collapsing wouldn't decrease the number of edges at all, while 1 means would be no edge left."


  def report(result: CohesionAnalysisResult): AnalysisResultsReporter.Report = {

    var fileNames = s"${packageCohesionsFileName}, ${verticalGroupingFileName}"

    new File(reportDirectory).mkdir()

    ExistingPackageCohesionsFileWriter.writeToFile(result)
    VerticalGroupingFileWriter.writeToFile(result.groupingResult)
    if (result.cohesiveNodeGroups.isDefined) {
      CohesiveGroupsFileWriter.writeToFile(result.cohesiveNodeGroups.get)
      fileNames += s", ${cohesiveGroupsFileName}"
    }

    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tPackage Cohesion Analysis\t")
    sb.append("\n" + sectionLine + "\n")
    sb.append(s"\nFor details check files: ${fileNames} in ${reportDirectory}\n\n")

    sb.append(sectionLine + "\n")

    sb.toString()
  }

  def round(d: Double) = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble


}