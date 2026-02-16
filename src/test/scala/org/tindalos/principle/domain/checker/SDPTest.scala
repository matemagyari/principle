package org.tindalos.principle.domain.checker

import org.junit._
import org.junit.Assert.assertEquals
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.agentscore.AnalysisInput
import org.tindalos.principle.domain.analyzers.sdp.SDPResult
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

class SDPTest {

  var plan: AnalysisPlan = _
  val analysisRunner= PoorMansDIContainer.buildAnalysisRunner()
  val checks = Constraints.builder().packageCoupling(PackageCouplingConstraints.builder().sdp(new SDP()).build()).build()

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
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val result = analysisRunner.run(new AnalysisInput(packageList, Set(), plan))
    println(s"result: $result")
    assertEquals(1, result.length)
    result.head.asInstanceOf[SDPResult]
  }

}