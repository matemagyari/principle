package org.tindalos.principle.domain.checker

import org.junit._
import org.junit.Assert.assertEquals
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.coredetector.AnalysisInput
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks

class SDPTest {

  var plan: AnalysisPlan = null
  val runAnalysis= PoorMansDIContainer.buildRunAnalysisFn()
  var checks: Expectations = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(checks, basePackage)
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
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, plan))
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