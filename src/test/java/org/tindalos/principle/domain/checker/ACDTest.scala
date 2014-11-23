package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.junit.Test
import org.junit._
import org.tindalos.principle.infrastructure.plugin.Checks
import org.tindalos.principle.domain.detector.acd._
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.expectations.cumulativedependency._

import org.junit.Assert.assertEquals

class ACDTest {

  var designQualityCheckConfiguration: DesignQualityCheckConfiguration = null
  var designQualityCheckService: (DesignQualityCheckConfiguration => DesignQualityCheckResults) = null
  var checks: DesignQualityExpectations = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  def init(basePackage: String) = {
    designQualityCheckService = PoorMansDIContainer.buildDesignChecker(basePackage)
    designQualityCheckConfiguration = new DesignQualityCheckConfiguration(checks, basePackage)
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
    val result = designQualityCheckService(designQualityCheckConfiguration)
    assertEquals(1, result.checkResults.length)
    result.checkResults.head.asInstanceOf[ACDResult].acd
  }

  private def prepareChecks() = new Checks(packageCoupling())

  private def packageCoupling() = {
    val packageCoupling = new PackageCoupling()
    packageCoupling.acd = new ACD()
    packageCoupling
  }

}