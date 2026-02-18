package org.tindalos.principle.infrastructure.analyzers.submodulesblueprint

import org.junit.Assert._
import org.junit._
import org.tindalos.principle.domain.analyzers.submodulesblueprint._

class YAMLBasedSubmodulesBlueprintProviderTest {

  @Test
  def readSubmoduleDefinitions_validYaml_parsesSuccessfully(): Unit = {
    val result = YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_ok.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )

    assertNotNull("SubmoduleDefinitions should not be null", result)
    val definitions = result.getDefinitions()
    assertEquals("Should have 3 module definitions", 3, definitions.size())
  }

  @Test
  def readSubmoduleDefinitions_validYaml_containsExpectedModules(): Unit = {
    val result = YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_ok.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )

    val definitions = result.getDefinitions()

    // Verify MOD1 exists
    val mod1Id = new SubmoduleId("MOD1")
    assertTrue("Should contain MOD1", definitions.containsKey(mod1Id))

    // Verify MOD2 exists
    val mod2Id = new SubmoduleId("MOD2")
    assertTrue("Should contain MOD2", definitions.containsKey(mod2Id))

    // Verify MOD3 exists
    val mod3Id = new SubmoduleId("MOD3")
    assertTrue("Should contain MOD3", definitions.containsKey(mod3Id))
  }

  @Test
  def readSubmoduleDefinitions_validYaml_modulesHaveCorrectPackages(): Unit = {
    val result = YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_ok.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )

    val definitions = result.getDefinitions()
    val mod1 = definitions.get(new SubmoduleId("MOD1"))

    assertNotNull("MOD1 should exist", mod1)
    val packages = mod1.packages()
    assertEquals("MOD1 should have 2 packages", 2, packages.size())

    import scala.collection.JavaConverters._
    val packageNames = packages.asScala.map(_.toString).toSet
    assertTrue("Should contain domain.mod1 package",
      packageNames.exists(_.contains("domain.mod1")))
    assertTrue("Should contain app.mod1 package",
      packageNames.exists(_.contains("app.mod1")))
  }

  @Test
  def readSubmoduleDefinitions_validYaml_modulesHaveCorrectDependencies(): Unit = {
    val result = YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_ok.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )

    val definitions = result.getDefinitions()
    val mod1 = definitions.get(new SubmoduleId("MOD1"))
    val mod2 = definitions.get(new SubmoduleId("MOD2"))

    // MOD1 should depend on MOD2
    import scala.collection.JavaConverters._
    val mod1Deps = mod1.getLegalDependencies().asScala
    assertTrue("MOD1 should depend on MOD2",
      mod1Deps.exists(_.value == "MOD2"))

    // MOD2 should depend on MOD1
    val mod2Deps = mod2.getLegalDependencies().asScala
    assertTrue("MOD2 should depend on MOD1",
      mod2Deps.exists(_.value == "MOD1"))
  }

  @Test
  def readSubmoduleDefinitions_validYaml_mod3HasNoDependencies(): Unit = {
    val result = YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_ok.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )

    val definitions = result.getDefinitions()
    val mod3 = definitions.get(new SubmoduleId("MOD3"))

    val mod3Deps = mod3.getLegalDependencies()
    assertEquals("MOD3 should have no dependencies", 0, mod3Deps.size())
  }

  @Test(expected = classOf[InvalidBlueprintDefinitionException])
  def readSubmoduleDefinitions_missingFile_throwsException(): Unit = {
    YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/non_existent_file.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )
  }

  @Test(expected = classOf[InvalidBlueprintDefinitionException])
  def readSubmoduleDefinitions_missingModuleDefinitions_throwsException(): Unit = {
    YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_missing_definitions.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )
  }

  @Test(expected = classOf[InvalidBlueprintDefinitionException])
  def readSubmoduleDefinitions_missingModuleDependencies_throwsException(): Unit = {
    YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_missing_dependencies.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )
  }

  @Test(expected = classOf[OverlappingSubmoduleDefinitionsException])
  def readSubmoduleDefinitions_overlappingModules_throwsException(): Unit = {
    YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_test_overlapping.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )
  }

  @Test
  def readSubmoduleDefinitions_basePackageNamePrependedCorrectly(): Unit = {
    val result = YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions(
      "src/test/resources/principle_blueprint_ok.yaml",
      "org.tindalos.principletest.submodulesblueprint"
    )

    val definitions = result.getDefinitions()
    val mod1 = definitions.get(new SubmoduleId("MOD1"))

    import scala.collection.JavaConverters._
    val packageRefs = mod1.packages().asScala

    // Verify all packages start with the base package name
    assertTrue("All packages should start with base package name",
      packageRefs.forall(_.toString.startsWith("org.tindalos.principletest.submodulesblueprint")))
  }
}

