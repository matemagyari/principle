package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.coredetector.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.detector.layering.{LayerReference, LayerViolationsResult}
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks

class LayeringTest {

  var plan: AnalysisPlan = null
  var runAnalysis = PoorMansDIContainer.buildRunAnalysisFn()
  var expectations: Expectations = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() = {

    val result = run("org.tindalos.principletest.layering.simple")
    val expected = Set(new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.app"),
      new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.infrastructure"),
      new LayerReference("org.tindalos.principletest.layering.simple.app", "org.tindalos.principletest.layering.simple.infrastructure"))
    assertEquals(expected, result.toSet)
  }

  @Test
  def deeper() = {

    val result = run("org.tindalos.principletest.layering.deeper")
    val expected = Set(new LayerReference("org.tindalos.principletest.layering.deeper.domain.aaa", "org.tindalos.principletest.layering.deeper.app.bbb.ccc"))
    assertEquals(expected, result.toSet)
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(expectations, basePackage)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[LayerViolationsResult].violations
  }

  private def prepareChecks() = new Checks(layering())

  private def layering() = {
    val layering = new Layering()
    layering.layers = List("infrastructure", "app", "domain")
    layering
  }

}