package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.sap.SAPResult
import org.tindalos.principle.domain.constraints.SAP
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.packages.{PackageMetrics, PackageReference}

class SAPAnalysisResultReporterTest {

  private val SEP = "=============================================================="
  private val sap = new SAP(0, 0.25)

  private def testPackage(name: String, distance: Float): Package = {
    val m = new PackageMetrics(0, 0, 0, 0, distance)
    new Package(name) {
      override def isUnreferred() = false
      override def getMetrics() = m
      override def getOwnPackageReferences() = Set.empty[PackageReference]
      override def getOwnExternalPackageReferences() = Set.empty[PackageReference]
    }
  }

  @Test
  def noViolations_reportsNoViolations(): Unit = {
    val result = SAPResult(List.empty, sap)

    val report = SAPAnalysisResultReporter.report(result)

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
    val result = SAPResult(List(pkg), sap)

    val report = SAPAnalysisResultReporter.report(result)

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
    val result = SAPResult(List(pkg), new SAP(3, 0.25))

    val report = SAPAnalysisResultReporter.report(result)

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
    val result = SAPResult(List(pkg1, pkg2), sap)

    val report = SAPAnalysisResultReporter.report(result)

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

