package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.adp.ADPResult
import org.tindalos.principle.domain.constraints.ADP
import org.tindalos.principle.domain.core.Cycle
import org.tindalos.principle.domain.core.packages.PackageReference

import java.util
import scala.collection.JavaConverters._

class ADPAnalysisResultReporterTest {

  private val SEP = "=============================================================="
  private val adp = new ADP(0)

  private def ref(name: String) = new PackageReference(name)

  @Test
  def noViolations_reportsNoViolations(): Unit = {
    val result = new ADPResult(Map.empty[PackageReference, util.Set[Cycle]].asJava, adp)

    val report = ADPAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tAcyclic Package Dependency Principle violations (0 of the allowed 0)\t
         |$SEP
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withThreshold_reportsThreshold(): Unit = {
    val result = new ADPResult(Map.empty[PackageReference, util.Set[Cycle]].asJava, new ADP(3))

    val report = ADPAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tAcyclic Package Dependency Principle violations (0 of the allowed 3)\t
         |$SEP
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withViolations_reportsBreakingPointsAndWritesFile(): Unit = {
    new java.io.File("./principle_reports").mkdirs()
    val cycle = new Cycle(List(ref("com.example.a"), ref("com.example.b")).asJava)
    val cycles: util.Set[Cycle] = Set(cycle).asJava
    val result = new ADPResult(Map(ref("com.example.a") -> cycles).asJava, adp)

    val report = ADPAnalysisResultReporter.report(result)

    val cycleDetailsFile = s"${ReportsDirectoryManager.reportDirectoryPath}/cycle_details.txt"
    val sp = " "
    val expected =
      s"""
         |$SEP
         |\tAcyclic Package Dependency Principle violations (1 of the allowed 0)\t
         |$SEP
         |$SEP
         |The cycles could be broken up refactoring the following packages:$sp
         |
         |com.example.a (1)
         |
         |For details check file: $cycleDetailsFile$sp
         |
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}

