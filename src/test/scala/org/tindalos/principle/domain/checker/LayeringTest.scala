package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.agentscore.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.agents.layering.{LayerReference, LayerViolationsResult}
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

class LayeringTest {

  var plan: AnalysisPlan = null
  var runAnalysis = PoorMansDIContainer.buildRunAnalysisFn()
  var expectations: Checks = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() = {

    val result = run("org.tindalos.principletest.layering.simple")
    val expected = Set(LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.app"),
      LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.infrastructure"),
      LayerReference("org.tindalos.principletest.layering.simple.app", "org.tindalos.principletest.layering.simple.infrastructure"))
    assertEquals(expected, result.toSet)
  }

  @Test
  def deeper() = {

    val result = run("org.tindalos.principletest.layering.deeper")
    val expected = Set(LayerReference("org.tindalos.principletest.layering.deeper.domain.aaa", "org.tindalos.principletest.layering.deeper.app.bbb.ccc"))
    assertEquals(expected, result.toSet)
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(expectations, basePackage)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, Set(), plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[LayerViolationsResult].violations
  }

  private def prepareChecks() = new Checks(layering())

  private def layering() = Layering(List("infrastructure", "app", "domain"))

}