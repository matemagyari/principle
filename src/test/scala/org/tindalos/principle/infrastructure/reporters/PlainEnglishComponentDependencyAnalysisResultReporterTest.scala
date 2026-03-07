package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult
import org.tindalos.principle.domain.constraints.{PackageCouplingConstraints, RACD}

class PlainEnglishComponentDependencyAnalysisResultReporterTest {

  private val SEP = "=============================================================="
  private val reporter = new PlainEnglishComponentDependencyAnalysisResultReporter()

  private def buildResult(cumulatedDeps: Int, numComponents: Int,
                          racdThreshold: Option[Double] = None): ComponentDependenciesResult = {
    val builder = PackageCouplingConstraints.builder()
    racdThreshold.foreach(t => builder.racd(new RACD(t)))
    new ComponentDependenciesResult(cumulatedDeps, numComponents, builder.build())
  }

  @Test
  def noRacdThreshold_reportsDefaultThreshold(): Unit = {
    val result = buildResult(cumulatedDeps = 10, numComponents = 5)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Component Dependency Metrics\t
         |$SEP
         |Relative Average Component Dependency:\t0.4( allowed 999.0)
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withRacdThreshold_reportsConfiguredThreshold(): Unit = {
    val result = buildResult(cumulatedDeps = 10, numComponents = 5, racdThreshold = Some(0.5))

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Component Dependency Metrics\t
         |$SEP
         |Relative Average Component Dependency:\t0.4( allowed 0.5)
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def singleComponent_reportsRacdOfOne(): Unit = {
    val result = buildResult(cumulatedDeps = 1, numComponents = 1)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Component Dependency Metrics\t
         |$SEP
         |Relative Average Component Dependency:\t1.0( allowed 999.0)
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}

