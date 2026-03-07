package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.sap.SAPResult
import org.tindalos.principle.domain.constraints.SAP
import org.tindalos.principle.domain.core.packages.{PackageMetrics, PackageReference, PackageWithMetrics}

import scala.collection.JavaConverters._

class PlainEnglishSAPAnalysisResultReporterTest {

  private val SEP = "=============================================================="
  private val sap = new SAP(0, 0.25)
  private val reporter = new PlainEnglishSAPAnalysisResultReporter()

  private def testPackage(name: String, distance: Float): PackageWithMetrics = new PackageWithMetrics {
    override def reference() = new PackageReference(name)
    override def getMetrics() = new PackageMetrics(0, 0, 0, 0, distance)
  }

  @Test
  def noViolations_reportsNoViolations(): Unit = {
    val result = new SAPResult(List.empty[PackageWithMetrics].asJava, sap)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tStable Abstractions Principle violations (0 of allowed 0 )\t
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withViolation_reportsPackageAndDistance(): Unit = {
    val pkg = testPackage("com.example.domain", 0.7f)
    val result = new SAPResult(List[PackageWithMetrics](pkg).asJava, sap)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tStable Abstractions Principle violations (1 of allowed 0 )\t
         |$SEP
         |${pkg.reference} 0.7
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withThreshold_reportsThreshold(): Unit = {
    val pkg = testPackage("com.example.domain", 0.7f)
    val result = new SAPResult(List(pkg).asJava, new SAP(3, 0.25))

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tStable Abstractions Principle violations (1 of allowed 3 )\t
         |$SEP
         |${pkg.reference} 0.7
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def multipleViolations_allReported(): Unit = {
    val pkg1 = testPackage("com.example.domain", 0.7f)
    val pkg2 = testPackage("com.example.app", 0.5f)
    val result = new SAPResult(List(pkg1, pkg2).asJava, sap)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tStable Abstractions Principle violations (2 of allowed 0 )\t
         |$SEP
         |${pkg1.reference} 0.7
         |${pkg2.reference} 0.5
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}

