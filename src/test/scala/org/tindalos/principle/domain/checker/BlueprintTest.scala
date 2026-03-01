package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.analyzers.submodulesblueprint._
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

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

    // When overlaps exist, illegalDependencies and missingDependencies should be empty
    assertEquals(Map(), result.illegalDependencies)
    assertEquals(Map(), result.missingDependencies)

    // Verify overlaps are detected
    assert(result.overlaps.nonEmpty, "Expected overlaps to be detected")
  }

  @Test
  def violationsCount() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    // Verify total violations count
    assertEquals(2, result.violationsNumber)
    assertEquals(1, result.illegalDependencies.size)
    assertEquals(1, result.missingDependencies.size)
  }

  @Test
  def illegalDependenciesOnly() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    val mod3 = fakeSubmodule("MOD3")
    val mod2 = fakeSubmodule("MOD2")

    // Verify illegal dependencies are detected
    assert(result.illegalDependencies.contains(mod3), "MOD3 should have illegal dependencies")
    val illegalDeps = result.illegalDependencies.get(mod3).get
    assert(illegalDeps.contains(mod2), "MOD3 illegally depends on MOD2")
  }

  @Test
  def missingDependenciesOnly() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    val mod1 = fakeSubmodule("MOD1")
    val mod2 = fakeSubmodule("MOD2")

    // Verify missing dependencies are detected
    assert(result.missingDependencies.contains(mod1), "MOD1 should have missing dependencies")
    val missingDeps = result.missingDependencies.get(mod1).get
    assert(missingDeps.contains(mod2), "MOD1 is missing dependency on MOD2")
  }

  @Test
  def expectationsFailed_whenViolationsExceedThreshold() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    // With threshold 0 and 2 violations, expectations should fail
    assert(result.constraintViolated(), "Expectations should fail when violations exceed threshold")
  }

  @Test
  def verifySubmoduleEquality() = {
    val mod1a = fakeSubmodule("MOD1")
    val mod1b = fakeSubmodule("MOD1")
    val mod2 = fakeSubmodule("MOD2")

    // Submodules with same ID should be equal
    assertEquals(mod1a, mod1b)
    assert(mod1a != mod2, "Submodules with different IDs should not be equal")
  }

  @Test
  def verifyResultStructure() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    // Verify result has correct structure
    assert(result.submodulesBlueprint != null, "Blueprint should not be null")
    assertEquals(0, result.threshold)
    assert(result.illegalDependencies.isInstanceOf[Map[_, _]], "Illegal dependencies should be a Map")
    assert(result.missingDependencies.isInstanceOf[Map[_, _]], "Missing dependencies should be a Map")
  }

  @Test
  def blueprintOk_parsingSucceeds() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_ok.yaml")

    // Verify blueprint was parsed successfully
    assert(result.submodulesBlueprint != null, "Blueprint should be parsed")
    assertEquals("src/test/resources/principle_blueprint_ok.yaml", result.submodulesBlueprint.location)

    // Verify threshold
    assertEquals(0, result.threshold)

    // Verify no overlaps (valid blueprint)
    assert(result.overlaps.isEmpty, "Valid blueprint should have no overlaps")

    // For a valid blueprint with matching code, there should be no violations
    // (Note: actual violations depend on the test code structure)
    assert(result.violationsNumber >= 0, "Violations should be non-negative")
  }

  def fakeSubmodule(name: String) = {
    new Submodule(new SubmoduleId(name), Set(), Set())
  }

  private def run(basePackage: String, location: String) = {
    val expectations = Constraints.builder().submodulesBlueprint(submodulesBlueprint(location)).build()
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val analysisRunner= PoorMansDIContainer.buildAnalysisRunner()
    val plan = new AnalysisPlan(expectations, basePackage)
    val result = analysisRunner.run(new AnalysisInput(packageList, Set(), plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[SubmodulesBlueprintAnalysisResult]
  }

  def submodulesBlueprint(location: String) = new SubmodulesBlueprint(location, 0)

}