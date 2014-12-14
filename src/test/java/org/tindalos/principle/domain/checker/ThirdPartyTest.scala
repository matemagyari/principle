package org.tindalos.principle.domain.checker

import org.junit.Assert._
import org.junit._
import org.tindalos.principle.domain.core.{AnalysisPlan, PackageReference}
import org.tindalos.principle.domain.coredetector.AnalysisInput
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyViolationsResult

class ThirdPartyTest {

  val runAnalysis= PoorMansDIContainer.buildRunAnalysisFn()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() {

    val barriers = List(new Barrier("app", "org.apache.commons.lang3"))
    val thirdParty = new ThirdParty(barriers)

    val result = run("org.tindalos.principletest.thirdparty.simple",thirdParty).asInstanceOf[ThirdPartyViolationsResult]
    val expected = Set((new PackageReference("org.tindalos.principletest.thirdparty.simple.domain")
      , new PackageReference("org.apache.commons.lang3")))
    assertEquals(expected, result.violations.toSet)
  }

  @Test
  def allowBoth() {

    val barriers = List(new Barrier("app", "org.apache.commons.lang3,org.apache.commons.io"))
    val thirdParty = new ThirdParty(barriers)

    val result = run("org.tindalos.principletest.thirdparty.simple2",thirdParty).asInstanceOf[ThirdPartyViolationsResult]

    assertTrue(result.violations.toSet.isEmpty)
  }


  @Test
  def allowOneRejectTheOther() {

    val barriers = List(new Barrier("app", "org.apache.commons.lang3"))
    val thirdParty = new ThirdParty(barriers)

    val result = run("org.tindalos.principletest.thirdparty.simple2",thirdParty).asInstanceOf[ThirdPartyViolationsResult]

    val expected = Set((new PackageReference("org.tindalos.principletest.thirdparty.simple2.app")
      , new PackageReference("org.apache.commons.io")))

    assertEquals(expected, result.violations.toSet)
  }


  private def run(basePackage: String, thirdParty:ThirdParty) = {
    val expectations: Expectations = new Checks(layering(), thirdParty)
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val plan = new AnalysisPlan(expectations, basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, plan))
    result(1)
  }

  private def layering() = {
    val layers = List("infrastructure", "app", "domain")
    new Layering(layers)
  }

}