package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.junit.Test
import org.junit._
import org.tindalos.principle.infrastructure.plugin.Checks
import org.tindalos.principle.domain.detector.sdp._
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.expectations.cumulativedependency._

import org.junit.Assert.assertEquals

class SDPTest {

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
  def simple() {

    val result = run("org.tindalos.principletest.sdp")

    result.violations.foreach({ println(_) })
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val result = designQualityCheckService(designQualityCheckConfiguration)
    assertEquals(1, result.checkResults.length)
    result.checkResults.head.asInstanceOf[SDPResult]
  }

  private def prepareChecks() = new Checks(packageCoupling())

  private def packageCoupling() = {
    val packageCoupling = new PackageCoupling()
    packageCoupling.sdp = new SDP()
    packageCoupling
  }

}