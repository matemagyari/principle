package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.agents.acd._
import org.tindalos.principle.domain.agentscore.AnalysisInput
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

class ACDTest {

  var plan: AnalysisPlan = null
  val runAnalysis = PoorMansDIContainer.buildRunAnalysisFn()
  var expectations: Checks = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(expectations, basePackage)
  }

  @Test
  def simple1() = {

    val result = run("org.tindalos.principletest.acd.simple1")

    assertEquals(1, result, 0.01)
  }

  @Test
  def simple11() {

    val result = run("org.tindalos.principletest.acd.simple11")

    assertEquals(1.5, result, 0.01)
  }

  @Test
  def simple() {

    val result = run("org.tindalos.principletest.acd.simple")

    assertEquals(2.5, result, 0.01)
  }

  @Test
  def cyclic3() {

    val result = run("org.tindalos.principletest.acd.cyclic3")

    assertEquals(3, result, 0.01)
  }

  @Test
  def cyclic6() {

    val result = run("org.tindalos.principletest.acd.cycle6")

    assertEquals(4.33, result, 0.01)
  }

  @Test
  def cyclic62() {

    val result = run("org.tindalos.principletest.acd.cycle6_2")

    assertEquals(2, result, 0.01)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, Set(), plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[ACDResult].acd
  }

  private def prepareChecks() = Checks(packageCoupling = packageCoupling())

  private def packageCoupling() = PackageCoupling(acd = ACD())

}