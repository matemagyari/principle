package org.tindalos.principle.domain.checker

import org.junit._
import org.junit.Assert.assertEquals
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.agentscore.AnalysisInput
import org.tindalos.principle.domain.agents.sdp.SDPResult
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

class SDPTest {

  var plan: AnalysisPlan = _
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
    val result = runAnalysis(new AnalysisInput(packageList, Set(), plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[SDPResult]
  }

  private def prepareChecks() = new Checks(packageCoupling = packageCoupling())

  private def packageCoupling() = PackageCoupling(sdp = new SDP())

}