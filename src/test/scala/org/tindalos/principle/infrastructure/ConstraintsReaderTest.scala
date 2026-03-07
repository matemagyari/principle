package org.tindalos.principle.infrastructure

import org.junit.Assert._
import org.junit.{After, Test}
import org.tindalos.principle.domain.constraints.exception.InvalidConfigurationException
import org.tindalos.principle.infrastructure.plugin.ConstraintsReader

import java.io.File
import java.nio.file.Files
import java.util.Collections

class ConstraintsReaderTest {

  private var tempFile: File = _

  @After
  def cleanup(): Unit = {
    if (tempFile != null && tempFile.exists()) tempFile.delete()
  }

  private def writeTempYaml(content: String): String = {
    tempFile = Files.createTempFile("principle_test_", ".yml").toFile
    Files.writeString(tempFile.toPath, content)
    tempFile.getAbsolutePath
  }

  @Test
  def rootPackage_isParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val rootPackage = plan.basePackage()

    assertEquals("com.example", rootPackage)
  }

  @Test
  def layering_isParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  layering:
        |    layers: [infrastructure, app, domain]
        |    violation_threshold: 2
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    val layering = constraints.layering().get()
    assertEquals(java.util.List.of("infrastructure", "app", "domain"), layering.layers())
    assertEquals(2, layering.violationThreshold())
  }

  @Test
  def layering_defaultThresholdIsZero(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  layering:
        |    layers: [a, b]
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertEquals(0, constraints.layering().get().violationThreshold())
  }

  @Test
  def noLayering_layeringIsNull(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertTrue(constraints.layering().isEmpty)
  }

  @Test
  def packageCoupling_adpThreshold_isParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 5
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    val adp = constraints.packageCoupling().get().adp()
    assertTrue(adp.isPresent)
    assertEquals(5, adp.get().violationThreshold())
  }

  @Test
  def packageCoupling_racdThreshold_isParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    acd_threshold: 0.35
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    val racd = constraints.packageCoupling().get().racd()
    assertTrue(racd.isPresent)
    assertEquals(0.35, racd.get().threshold(), 0.001)
  }

  @Test
  def structureAnalysisEnabled_groupingIsPresent(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |structure_analysis_enabled: true
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertTrue(constraints.packageCoupling().get().grouping().isPresent)
  }

  @Test
  def structureAnalysisDisabled_groupingIsAbsent(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |structure_analysis_enabled: false
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertFalse(constraints.packageCoupling().get().grouping().isPresent)
  }

  @Test
  def thirdPartyRestrictions_isParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |  third_party_restrictions:
        |    allowed_libraries:
        |      - layer: infrastructure
        |        libraries: [org.apache.commons, com.google]
        |      - layer: app
        |        libraries: [org.apache.commons]
        |    violation_threshold: 3
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    val tp = constraints.thirdParty()
    assertTrue(tp.isPresent)
    assertEquals(3, tp.get().violationThreshold())
    assertEquals(2, tp.get().barriers().size())
    assertEquals("infrastructure", tp.get().barriers().get(0).layer())
    assertEquals(java.util.List.of("org.apache.commons", "com.google"), tp.get().barriers().get(0).components())
    assertEquals("app", tp.get().barriers().get(1).layer())
    assertEquals(Collections.singletonList("org.apache.commons"), tp.get().barriers().get(1).components())
  }

  @Test
  def noThirdPartyRestrictions_thirdPartyIsAbsent(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertFalse(constraints.thirdParty().isPresent)
  }

  @Test
  def modules_withThreeModuleDefinitions_blueprintIsParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  modules:
        |    module-definitions:
        |      AUTH: [domain.auth, app.auth]
        |      BILLING: [domain.billing, app.billing]
        |      NOTIFICATION: [domain.notification, app.notification]
        |    module-dependencies:
        |      AUTH: [BILLING]
        |      BILLING: [NOTIFICATION]
        |    violation_threshold: 2
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val blueprint = plan.constraints().submodulesBlueprint().get()

    assertEquals(path, blueprint.location())
    assertEquals(2, blueprint.violationThreshold())
  }

  @Test
  def modules_isParsed(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |  modules:
        |    violation_threshold: 1
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertTrue(constraints.submodulesBlueprint().isPresent)
    assertEquals(1, constraints.submodulesBlueprint().get().violationThreshold())
  }

  @Test
  def modules_locationPointsToConfigFile(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |  modules:
        |    violation_threshold: 3
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val blueprint = plan.constraints().submodulesBlueprint().get()

    assertEquals(path, blueprint.location())
    assertEquals(3, blueprint.violationThreshold())
  }

  @Test
  def modules_defaultThresholdIsZero(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |  modules: {}
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val blueprint = plan.constraints().submodulesBlueprint().get()

    assertEquals(0, blueprint.violationThreshold())
  }

  @Test
  def noModules_submodulesBlueprintIsAbsent(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: com.example
        |checks:
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()

    assertFalse(constraints.submodulesBlueprint().isPresent)
  }

  @Test(expected = classOf[InvalidConfigurationException])
  def missingFile_throwsInvalidConfigurationException(): Unit = {
    ConstraintsReader.readFromFile(Some("/non/existent/path/principle.yml"))
  }

  @Test
  def fullConfig_isParsedCorrectly(): Unit = {
    val path = writeTempYaml(
      """
        |root_package: org.example.myapp
        |checks:
        |  layering:
        |    layers: [infrastructure, app, domain]
        |    violation_threshold: 0
        |  third_party_restrictions:
        |    allowed_libraries:
        |      - layer: infrastructure
        |        libraries: [org.apache.commons]
        |    violation_threshold: 0
        |  package_coupling:
        |    cyclic_dependencies_threshold: 0
        |    acd_threshold: 0.5
        |  modules:
        |    violation_threshold: 0
        |structure_analysis_enabled: true
        |""".stripMargin)

    val plan = ConstraintsReader.readFromFile(Some(path))
    val constraints = plan.constraints()
    val rootPackage = plan.basePackage()

    assertEquals("org.example.myapp", rootPackage)
    assertTrue(constraints.layering().isPresent)
    assertTrue(constraints.thirdParty().isPresent)
    assertTrue(constraints.packageCoupling().isPresent)
    assertTrue(constraints.packageCoupling().get().adp().isPresent)
    assertTrue(constraints.packageCoupling().get().racd().isPresent)
    assertTrue(constraints.packageCoupling().get().grouping().isPresent)
    assertTrue(constraints.submodulesBlueprint().isPresent)
  }
}
