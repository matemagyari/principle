package org.tindalos.principle.infrastructure.analyzers.submodulesblueprint

import org.junit.Assert._
import org.junit._
import org.tindalos.principle.domain.analyzers.submodulesblueprint._

import java.io.File
import org.tindalos.principle.domain.core.PackageReference

import java.util.{Collections}
import org.apache.commons.io.FileUtils

import java.util

class YAMLBasedSubmodulesBlueprintProviderTest {

  val yamlFile = createTempYamlFile(
    """
      |checks:
      |  modules:
      |    module-definitions:
      |      MOD1: [domain.mod1, app.mod1]
      |      MOD2: [domain.mod2, app.mod2]
      |      MOD3: [domain.mod3, app.mod3]
      |    module-dependencies:
      |      MOD1: [MOD2, MOD3]
      |      MOD2: [MOD3]
      |      MOD3: []
      |    violation_threshold: 0
    """.stripMargin)

  private val provider = new YAMLBasedSubmodulesBlueprintProvider("com")

  private def createTempYamlFile(content: String): String = {
    val tempFile = File.createTempFile("test_blueprint_", ".yaml")
    tempFile.deleteOnExit()
    FileUtils.writeStringToFile(tempFile, content)
    tempFile.getAbsolutePath
  }

  @Test
  def readSubmoduleDefinitions_validYaml_parsesSuccessfully(): Unit = {

    val result = provider.readSubmoduleDefinitions(yamlFile)

    assertNotNull("SubmoduleDefinitions should not be null", result)

    val definitions: util.Map[SubmoduleId, SubmoduleDefinition] = result.getDefinitions()
    assertEquals(3, definitions.size())
    assertEquals(util.Set.of(
      new SubmoduleId("MOD1"),
      new SubmoduleId("MOD2"),
      new SubmoduleId("MOD3")), definitions.keySet())


    val definition1: SubmoduleDefinition = definitions.get(new SubmoduleId("MOD1"))
    assertEquals(util.Set.of(new SubmoduleId("MOD2"), new SubmoduleId("MOD3")), definition1.getLegalDependencies())
    assertEquals(util.Set.of(new PackageReference("com.domain.mod1"), new PackageReference("com.app.mod1")), definition1.packages())

    val definition2: SubmoduleDefinition = definitions.get(new SubmoduleId("MOD2"))
    assertEquals(Collections.singleton(new SubmoduleId("MOD3")), definition2.getLegalDependencies())
    assertEquals(util.Set.of(new PackageReference("com.domain.mod2"), new PackageReference("com.app.mod2")), definition2.packages())

    val definition3: SubmoduleDefinition = definitions.get(new SubmoduleId("MOD3"))
    assertTrue(definition3.getLegalDependencies().isEmpty)
    assertEquals(util.Set.of(new PackageReference("com.domain.mod3"), new PackageReference("com.app.mod3")), definition3.packages())

  }

  @Test(expected = classOf[InvalidBlueprintDefinitionException])
  def readSubmoduleDefinitions_missingFile_throwsException(): Unit = {
    provider.readSubmoduleDefinitions("src/test/resources/non_existent_file.yaml")
  }

  @Test(expected = classOf[InvalidBlueprintDefinitionException])
  def readSubmoduleDefinitions_missingModuleDefinitions_throwsException(): Unit = {
    val yamlFile = createTempYamlFile("""
                                        |checks:
                                        |  modules:
                                        |    module-dependencies:
                                        |      MOD1: [MOD2]
                                        |    violation_threshold: 0
    """.stripMargin)
    provider.readSubmoduleDefinitions(yamlFile)
  }

  @Test(expected = classOf[InvalidBlueprintDefinitionException])
  def readSubmoduleDefinitions_missingModuleDependencies_throwsException(): Unit = {
    val yaml =
      """
        |checks:
        |  modules:
        |    module-definitions:
        |      MOD1: [domain.mod1, app.mod1]
        |      MOD2: [domain.mod2, app.mod2]
        |    violation_threshold: 0
    """.stripMargin

    val yamlFile = createTempYamlFile(yaml)
    provider.readSubmoduleDefinitions(yamlFile)
  }

  @Test(expected = classOf[OverlappingSubmoduleDefinitionsException])
  def readSubmoduleDefinitions_overlappingModules_throwsException(): Unit = {
    val yamlFile = createTempYamlFile("""
                                        |checks:
                                        |  modules:
                                        |    module-definitions:
                                        |      MOD1: [domain]
                                        |      MOD2: [domain.mod2]
                                        |    module-dependencies:
                                        |      MOD1: []
                                        |      MOD2: []
                                        |    violation_threshold: 0
    """.stripMargin)
    provider.readSubmoduleDefinitions(yamlFile)
  }
}

