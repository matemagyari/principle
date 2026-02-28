package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.layering.{LayerReference, LayerViolationsResult}

import scala.collection.JavaConverters._

class LayerAnalysisResultReporterTest {

  private val SEP = "=============================================================="

  @Test
  def noViolations_reportsNoViolations(): Unit = {
    val result = new LayerViolationsResult(List.empty[LayerReference].asJava, 0)

    val report = LayerAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tLayering violations (0 of allowed 0 )\t
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withViolation_reportsViolation(): Unit = {
    val violation = new LayerReference("com.example.domain", "com.example.infrastructure")
    val result = new LayerViolationsResult(List(violation).asJava, 0)

    val report = LayerAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tLayering violations (1 of allowed 0 )\t
         |$SEP
         |com.example.domain -> com.example.infrastructure
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withThreshold_reportsThreshold(): Unit = {
    val violation = new LayerReference("com.example.domain", "com.example.infrastructure")
    val result = new LayerViolationsResult(List(violation).asJava, 3)

    val report = LayerAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tLayering violations (1 of allowed 3 )\t
         |$SEP
         |com.example.domain -> com.example.infrastructure
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def multipleViolations_allReported(): Unit = {
    val violations = List(
      new LayerReference("com.example.domain", "com.example.app"),
      new LayerReference("com.example.domain", "com.example.infrastructure"),
      new LayerReference("com.example.app", "com.example.infrastructure")
    )
    val result = new LayerViolationsResult(violations.asJava, 0)

    val report = LayerAnalysisResultReporter.report(result)

    val expected =
      s"""
         |$SEP
         |\tLayering violations (3 of allowed 0 )\t
         |$SEP
         |com.example.domain -> com.example.app
         |com.example.domain -> com.example.infrastructure
         |com.example.app -> com.example.infrastructure
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}

