package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.core.ExpectationsConfig
import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.detector.sdp._
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks

class SDPTest {

  var designQualityCheckConfiguration: ExpectationsConfig = null
  var designQualityCheckService: (ExpectationsConfig => List[AnalysisResult]) = null
  var checks: Expectations = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  def init(basePackage: String) = {
    designQualityCheckService = PoorMansDIContainer.buildDesignChecker(basePackage)
    designQualityCheckConfiguration = new ExpectationsConfig(checks, basePackage)
  }

  @Test
  def simple() {

    val result = run("org.tindalos.principletest.sdp")

    result.violations.foreach({
      println(_)
    })
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val result = designQualityCheckService(designQualityCheckConfiguration)
    assertEquals(1, result.length)
    result.head.asInstanceOf[SDPResult]
  }

  private def prepareChecks() = new Checks(packageCoupling())

  private def packageCoupling() = {
    val packageCoupling = new PackageCoupling()
    packageCoupling.sdp = new SDP()
    packageCoupling
  }

}