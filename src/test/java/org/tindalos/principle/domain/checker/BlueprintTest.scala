package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.coredetector.AnalysisInput
import org.tindalos.principle.domain.detector.submodulesblueprint._
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks

class BlueprintTest {

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def missingAndIllegal() {

    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    val mod1 = fakeSubmodule("MOD1")
    val mod2 = fakeSubmodule("MOD2")
    val mod3 = fakeSubmodule("MOD3")

    assertEquals(Map(mod3 -> Set(mod2)), result.illegalDependencies)
    assertEquals(Map(mod1 -> Set(mod2)), result.missingDependencies)
  }

  @Test
  def overlapping() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test_overlapping.yaml")
    System.out.println(result)
  }

  def fakeSubmodule(name: String) = {
    new Submodule(new SubmoduleId(name), Set(), Set())
  }

  private def run(basePackage: String, location: String) = {
    val expectations = prepareChecks(location)
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val runAnalysis= PoorMansDIContainer.buildRunAnalysisFn()
    val plan = new AnalysisPlan(expectations, basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, Set(), plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[SubmodulesBlueprintAnalysisResult]
  }

  def prepareChecks(location: String) = {
    val checks = new Checks()
    checks.submodulesBlueprint = submodulesBlueprint(location)
    checks
  }

  def submodulesBlueprint(location: String) = new SubmodulesBlueprint(location, 0)

}