package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.sdp.{SDPResult, SDPViolation}
import org.tindalos.principle.domain.constraints.SDP
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.packages.{PackageMetrics, PackageReference}

class SDPAnalysisResultReporterTest {

  private val SEP = "=============================================================="
  private val sdp = new SDP(0)
  private val reporter = new SDPAnalysisResultReporter()

  private def testPackage(name: String, instability: Float): Package = {
    val m = new PackageMetrics(0, 0, 0, instability, 0)
    new Package(name) {
      override def isUnreferred() = false
      override def getMetrics() = m
      override def getOwnPackageReferences() = Set.empty[PackageReference]
      override def getOwnExternalPackageReferences() = Set.empty[PackageReference]
    }
  }

  @Test
  def noViolations_reportsNoViolations(): Unit = {
    val result = SDPResult(List.empty, sdp)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tStable Dependencies Principle violations (0 of allowed 0 )\t
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withViolation_reportsDependerAndDependee(): Unit = {
    val depender = testPackage("com.example.app", 0.8f)
    val dependee = testPackage("com.example.domain", 0.3f)
    val result = SDPResult(List(SDPViolation(depender, dependee)), sdp)

    val report = reporter.report(result)

    val sp = " "
    val expected =
      s"""
         |$SEP
         |\tStable Dependencies Principle violations (1 of allowed 0 )\t
         |$SEP
         |
         |$sp${depender.reference}[0.8] --> ${dependee.reference}[0.3]$sp
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withThreshold_reportsThreshold(): Unit = {
    val depender = testPackage("com.example.app", 0.8f)
    val dependee = testPackage("com.example.domain", 0.3f)
    val result = SDPResult(List(SDPViolation(depender, dependee)), new SDP(3))

    val report = reporter.report(result)

    val sp = " "
    val expected =
      s"""
         |$SEP
         |\tStable Dependencies Principle violations (1 of allowed 3 )\t
         |$SEP
         |
         |$sp${depender.reference}[0.8] --> ${dependee.reference}[0.3]$sp
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def multipleViolations_allReported(): Unit = {
    val pkg1 = testPackage("com.example.app", 0.8f)
    val pkg2 = testPackage("com.example.domain", 0.3f)
    val pkg3 = testPackage("com.example.infrastructure", 0.5f)
    val result = SDPResult(List(SDPViolation(pkg1, pkg2), SDPViolation(pkg3, pkg2)), sdp)

    val report = reporter.report(result)

    val sp = " "
    val expected =
      s"""
         |$SEP
         |\tStable Dependencies Principle violations (2 of allowed 0 )\t
         |$SEP
         |
         |$sp${pkg1.reference}[0.8] --> ${pkg2.reference}[0.3]$sp
         |
         |$sp${pkg3.reference}[0.5] --> ${pkg2.reference}[0.3]$sp
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}

