package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult
import org.tindalos.principle.domain.constraints.ThirdParty
import org.tindalos.principle.domain.core.packages.PackageReference

import java.util.Collections
import scala.collection.JavaConverters._

class PlainEnglishThirdPartyAnalysisResultReporterTest {

  private val thirdParty = new ThirdParty(Collections.emptyList(), 0)
  private val SEP = "=============================================================="
  private val reporter = new PlainEnglishThirdPartyAnalysisResultReporter()

  private def result(violations: Map[PackageReference, Set[PackageReference]], tp: ThirdParty = thirdParty) =
    new ThirdPartyViolationsResult(violations.map { case (k, v) => k -> v.asJava }.asJava, tp)

  @Test
  def noViolations_containsNoViolationsMessage(): Unit = {
    val report = reporter.report(result(Map.empty))

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
    val report = reporter.report(result(Map(referrer -> Set(dependency))))

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
    val report = reporter.report(result(Map(referrer -> Set(dependency)), thirdPartyWith3))

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
    val violations = Map(
      new PackageReference("com.example.app") -> Set(new PackageReference("org.apache.commons.io")),
      new PackageReference("com.example.domain") -> Set(new PackageReference("org.apache.commons.lang3"))
    )
    val report = reporter.report(result(violations))

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
    val violations = Map(
      new PackageReference("com.example.app") -> Set(
        new PackageReference("org.apache.commons.io"),
        new PackageReference("org.apache.commons.lang3")
      ),
      new PackageReference("com.example.domain") -> Set(new PackageReference("org.apache.commons.io"))
    )
    val report = reporter.report(result(violations))

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
