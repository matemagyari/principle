package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.junit.Test
import org.junit._
import org.tindalos.principle.infrastructure.plugin.Checks
import org.tindalos.principle.domain.expectations._
import org.junit.Assert.assertEquals
import org.tindalos.principle.domain.detector.submodulesblueprint._

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
    val checks = prepareChecks(location)
    val designQualityCheckService = PoorMansDIContainer.buildDesignChecker(basePackage)
    val result = designQualityCheckService(new DesignQualityCheckConfiguration(checks, basePackage))
    assertEquals(1, result.checkResults.length)
    result.checkResults.head.asInstanceOf[SubmodulesBlueprintCheckResult]
  }

  def prepareChecks(location: String) = {
    val checks = new Checks()
    checks.submodulesBlueprint = submodulesBlueprint(location)
    checks
  }

  def submodulesBlueprint(location: String) = new SubmodulesBlueprint(location, 0)

}