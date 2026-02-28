package org.tindalos.principle.domain.checker

import org.junit.Assert._
import org.junit._
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.agentscore.AnalysisInput
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult
import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder

import java.util.Collections

class ThirdPartyTest {

  val analysisRunner = PoorMansDIContainer.buildAnalysisRunner()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() {

    val barriers = Collections.singletonList(new Barrier("app", Collections.singletonList("org.apache.commons.lang3")))
    val thirdParty = new ThirdParty(barriers, 0)

    val result = run("org.tindalos.principletest.thirdparty.simple",thirdParty).asInstanceOf[ThirdPartyViolationsResult]
    val expected = Map(new PackageReference("org.tindalos.principletest.thirdparty.simple.domain") ->
      Set(new PackageReference("org.apache.commons.lang3")))
    assertEquals(expected, result.violations)
  }

  @Test
  def allowBoth() {

    val barriers = Collections.singletonList(new Barrier("app", java.util.List.of("org.apache.commons.lang3","org.apache.commons.io")))
    val thirdParty = new ThirdParty(barriers, 0)

    val result = run("org.tindalos.principletest.thirdparty.simple2",thirdParty).asInstanceOf[ThirdPartyViolationsResult]

    assertTrue(result.violations.isEmpty)
  }


  @Test
  def allowOneRejectTheOther() {

    val barriers = Collections.singletonList(new Barrier("app", Collections.singletonList("org.apache.commons.lang3")))
    val thirdParty = new ThirdParty(barriers, 0)

    val result = run("org.tindalos.principletest.thirdparty.simple2",thirdParty).asInstanceOf[ThirdPartyViolationsResult]

    val expected = Map(new PackageReference("org.tindalos.principletest.thirdparty.simple2.app") ->
      Set(new PackageReference("org.apache.commons.io")))
    assertEquals(expected, result.violations)
  }


  private def run(basePackage: String, thirdParty:ThirdParty) = {
    val expectations: Constraints = Constraints.builder().layering(layering()).thirdParty(thirdParty).build()
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val plan = new AnalysisPlan(expectations, basePackage)
    val result = analysisRunner.run(new AnalysisInput(packageList, Set(), plan))
    result(1)
  }

  private def layering() = {
    new Layering(java.util.List.of("infrastructure", "app", "domain"), 0)
  }

}