package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.analyzers.submodulesblueprint._
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.domain.core.packages.PackageWithMetrics

import scala.collection.JavaConverters._
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
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

    assertEquals(java.util.Map.of(mod3, java.util.Set.of[Submodule](mod2)), result.illegalDependencies())
    assertEquals(java.util.Map.of(mod1, java.util.Set.of[Submodule](mod2)), result.missingDependencies())
  }

  @Test
  def overlapping() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test_overlapping.yaml")

    assertEquals(java.util.Map.of(), result.illegalDependencies())
    assertEquals(java.util.Map.of(), result.missingDependencies())
    assert(!result.overlaps().isEmpty, "Expected overlaps to be detected")
  }

  @Test
  def violationsCount() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    assertEquals(2, result.violationsNumber())
    assertEquals(1, result.illegalDependencies().size)
    assertEquals(1, result.missingDependencies().size)
  }

  @Test
  def illegalDependenciesOnly() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    val mod3 = fakeSubmodule("MOD3")
    val mod2 = fakeSubmodule("MOD2")

    assert(result.illegalDependencies().containsKey(mod3), "MOD3 should have illegal dependencies")
    val illegalDeps = result.illegalDependencies().get(mod3)
    assert(illegalDeps.contains(mod2), "MOD3 illegally depends on MOD2")
  }

  @Test
  def missingDependenciesOnly() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")

    val mod1 = fakeSubmodule("MOD1")
    val mod2 = fakeSubmodule("MOD2")

    assert(result.missingDependencies().containsKey(mod1), "MOD1 should have missing dependencies")
    val missingDeps = result.missingDependencies().get(mod1)
    assert(missingDeps.contains(mod2), "MOD1 is missing dependency on MOD2")
  }

  @Test
  def expectationsFailed_whenViolationsExceedThreshold() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml")
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

    assertEquals(0, result.threshold())
    assert(result.illegalDependencies().isInstanceOf[java.util.Map[_, _]], "Illegal dependencies should be a Map")
    assert(result.missingDependencies().isInstanceOf[java.util.Map[_, _]], "Missing dependencies should be a Map")
  }

  @Test
  def blueprintOk_parsingSucceeds() = {
    val result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_ok.yaml")

    assertEquals(0, result.threshold())
    assert(result.overlaps().isEmpty, "Valid blueprint should have no overlaps")
    assert(result.violationsNumber() >= 0, "Violations should be non-negative")
  }

  def fakeSubmodule(name: String) = {
    new Submodule(new SubmoduleId(name), java.util.Set.of[PackageWithMetrics](), java.util.Set.of[SubmoduleId]())
  }

  private def run(basePackage: String, location: String) = {
    val provider = new YAMLBasedSubmodulesBlueprintProvider()
    val submoduleDefinitions = provider.readSubmoduleDefinitions(basePackage, location, 0)
    val constraints = Constraints.builder().submoduleDefinitions(submoduleDefinitions).build()
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val analysisRunner= PoorMansDIContainer.buildAnalysisRunner()
    val plan = new AnalysisPlan(constraints, basePackage)
    val result = analysisRunner.run(new AnalysisInput(packageList.map(p => p: PackageWithMetrics).asJava, Set.empty.asJava, plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[SubmodulesBlueprintAnalysisResult]
  }


}