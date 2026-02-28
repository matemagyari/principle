package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult
import org.tindalos.principle.domain.constraints.ThirdParty
import org.tindalos.principle.domain.core.packages.PackageReference

import java.util.Collections

class ThirdPartyAnalysisResultReporterTest {

  private val thirdParty = new ThirdParty(Collections.emptyList(), 0)

  private val SEP = "=============================================================="

  @Test
  def noViolations_containsNoViolationsMessage(): Unit = {
    val result = ThirdPartyViolationsResult(List.empty, thirdParty)

    val report = ThirdPartyAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |
         |Third party violations (0 of allowed 0 )\t
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withViolation_containsReferrerAndDependency(): Unit = {
    val referrer = new PackageReference("com.example.app")
    val dependency = new PackageReference("org.apache.commons.io")
    val result = ThirdPartyViolationsResult(List((referrer, dependency)), thirdParty)

    val report = ThirdPartyAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |
         |Third party violations (1 of allowed 0 )\t
         |$SEP
         |com.example.app refers to org.apache.commons.io
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withViolation_containsThresholdInfo(): Unit = {
    val thirdPartyWith3 = new ThirdParty(Collections.emptyList(), 3)
    val referrer = new PackageReference("com.example.app")
    val dependency = new PackageReference("org.apache.commons.io")
    val result = ThirdPartyViolationsResult(List((referrer, dependency)), thirdPartyWith3)

    val report = ThirdPartyAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |
         |Third party violations (1 of allowed 3 )\t
         |$SEP
         |com.example.app refers to org.apache.commons.io
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def multipleViolations_allListedInReport(): Unit = {
    val violations = List(
      (new PackageReference("com.example.app"), new PackageReference("org.apache.commons.io")),
      (new PackageReference("com.example.domain"), new PackageReference("org.apache.commons.lang3"))
    )
    val result = ThirdPartyViolationsResult(violations, thirdParty)

    val report = ThirdPartyAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |
         |Third party violations (2 of allowed 0 )\t
         |$SEP
         |com.example.app refers to org.apache.commons.io
         |com.example.domain refers to org.apache.commons.lang3
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def twoReferrersOneWithTwoDependencies_allListedInReport(): Unit = {
    val violations = List(
      (new PackageReference("com.example.app"), new PackageReference("org.apache.commons.io")),
      (new PackageReference("com.example.app"), new PackageReference("org.apache.commons.lang3")),
      (new PackageReference("com.example.domain"), new PackageReference("org.apache.commons.io"))
    )
    val result = ThirdPartyViolationsResult(violations, thirdParty)

    val report = ThirdPartyAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |
         |Third party violations (3 of allowed 0 )\t
         |$SEP
         |com.example.app refers to org.apache.commons.io
         |com.example.app refers to org.apache.commons.lang3
         |com.example.domain refers to org.apache.commons.io
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}
