package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.analyzers.structure.Graph.SubgraphDecomposition
import org.tindalos.principle.domain.analyzers.structure.PackageStructureHints1Finder.GroupingResult
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager

class PackageCohesionReporterTest {

  private val SEP = "================================================================================"
  private val reporter = new PlainEnglishPackageCohesionReporter()

  private val emptyResult = CohesionAnalysisResult(
    packages = Set.empty,
    cohesiveNodeGroups = None,
    groupingResult = GroupingResult(Map.empty, List.empty),
    subgraphDecomposition = SubgraphDecomposition(List.empty)
  )

  @Test
  def report_containsSectionHeaderAndFooter(): Unit = {
    new java.io.File("./principle_reports").mkdirs()

    val report = reporter.report(emptyResult)

    val reportsDir = ReportsDirectoryManager.ensureReportsDirectoryExists()
    val sp = " "
    val expected =
      s"""
         |$SEP
         |\tPackage Cohesion Analysis\t
         |$SEP
         |
         |For details check files: ${PackageCohesionReporter.packageCohesionsFileName}, ${PackageCohesionReporter.packageStructureHints1FileName}, ${PackageCohesionReporter.packageStructureHints2FileName} in $reportsDir
         |
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def report_withCohesiveGroups_includesCohesiveGroupsFile(): Unit = {
    new java.io.File("./principle_reports").mkdirs()

    val resultWithGroups = emptyResult.copy(cohesiveNodeGroups = Some(Set.empty))
    val report = reporter.report(resultWithGroups)

    assertTrue(report.contains(PackageCohesionReporter.cohesiveGroupsFileName))
  }

  @Test
  def report_withoutCohesiveGroups_doesNotIncludeCohesiveGroupsFile(): Unit = {
    new java.io.File("./principle_reports").mkdirs()

    val report = reporter.report(emptyResult)

    assertTrue(!report.contains(PackageCohesionReporter.cohesiveGroupsFileName))
  }
}

