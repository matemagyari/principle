package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.analyzers.structure.SubgraphDecomposition
import org.tindalos.principle.domain.analyzers.structure.GroupingResult
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager

class PlainEnglishPackageCohesionAnalysisResultReporterTest {

  private val SEP = "================================================================================"
  private val reporter = new PlainEnglishPackageCohesionAnalysisResultReporter()

  private val emptyResult = new CohesionAnalysisResult(
    java.util.Collections.emptyMap(),
    java.util.Optional.empty(),
    new GroupingResult(java.util.Collections.emptyMap(), java.util.Collections.emptyList()),
    new SubgraphDecomposition(java.util.Collections.emptyList())
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
         |For details check files: ${PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME}, ${PackageCohesionAnalysisResultReporter.packageStructureHints1FileName}, ${PackageCohesionAnalysisResultReporter.packageStructureHints2FileName} in $reportsDir
         |
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def report_withCohesiveGroups_includesCohesiveGroupsFile(): Unit = {
    new java.io.File("./principle_reports").mkdirs()

    val resultWithGroups = new CohesionAnalysisResult(
      java.util.Collections.emptyMap(),
      java.util.Optional.of(java.util.Collections.emptySet()),
      new GroupingResult(java.util.Collections.emptyMap(), java.util.Collections.emptyList()),
      new SubgraphDecomposition(java.util.Collections.emptyList())
    )
    val report = reporter.report(resultWithGroups)

    assertTrue(report.contains(PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME))
  }

  @Test
  def report_withoutCohesiveGroups_doesNotIncludeCohesiveGroupsFile(): Unit = {
    new java.io.File("./principle_reports").mkdirs()

    val report = reporter.report(emptyResult)

    assertTrue(!report.contains(PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME))
  }
}

