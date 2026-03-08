package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{Overlap, Submodule, SubmoduleId, SubmodulesBlueprintAnalysisResult}
import org.tindalos.principle.domain.core.Package
import org.yaml.snakeyaml.Yaml

class YAMLSubmodulesBlueprintAnalysisResultReporterTest {

  private val reporter = new YAMLSubmodulesBlueprintAnalysisResultReporter()

  private def submodule(id: String): Submodule =
    new Submodule(new SubmoduleId(id), Set.empty[Package], Set.empty[SubmoduleId])

  private def assertValidYaml(yaml: String): Unit = {
    val parsed = new Yaml().load(yaml)
    assert(parsed != null, "YAML must parse to a non-null object")
  }

  @Test
  def noViolations_reportsEmptyDependencies(): Unit = {
    val result = SubmodulesBlueprintAnalysisResult(0)

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 0
        |  threshold: 0
        |  constraint_violated: false
        |  illegal_dependencies: []
        |  missing_dependencies: []
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def noViolations_withThreshold_reportsThreshold(): Unit = {
    val result = SubmodulesBlueprintAnalysisResult(3)

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 0
        |  threshold: 3
        |  constraint_violated: false
        |  illegal_dependencies: []
        |  missing_dependencies: []
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withOverlap_reportsOverlap(): Unit = {
    val overlap = new Overlap(new SubmoduleId("MOD1"), new SubmoduleId("MOD2"))
    val result = SubmodulesBlueprintAnalysisResult(0, overlaps = Set(overlap))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 0
        |  threshold: 0
        |  constraint_violated: false
        |  overlaps: true
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withIllegalDependency_reportsIllegalDependency(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val result = SubmodulesBlueprintAnalysisResult(0, illegalDependencies = Map(mod1 -> Set(mod2)))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 1
        |  threshold: 0
        |  constraint_violated: true
        |  illegal_dependencies:
        |    - submodule: MOD1
        |      depends_on: [MOD2]
        |  missing_dependencies: []
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withMissingDependency_reportsMissingDependency(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val result = SubmodulesBlueprintAnalysisResult(0, missingDependencies = Map(mod1 -> Set(mod2)))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 1
        |  threshold: 0
        |  constraint_violated: true
        |  illegal_dependencies: []
        |  missing_dependencies:
        |    - submodule: MOD1
        |      depends_on: [MOD2]
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withIllegalAndMissingDependencies_reportsBoth(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val mod3 = submodule("MOD3")
    val result = SubmodulesBlueprintAnalysisResult(
      0,
      illegalDependencies = Map(mod1 -> Set(mod2)),
      missingDependencies = Map(mod1 -> Set(mod3)))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 2
        |  threshold: 0
        |  constraint_violated: true
        |  illegal_dependencies:
        |    - submodule: MOD1
        |      depends_on: [MOD2]
        |  missing_dependencies:
        |    - submodule: MOD1
        |      depends_on: [MOD3]
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withinThreshold_constraintNotViolated(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val result = SubmodulesBlueprintAnalysisResult(5, illegalDependencies = Map(mod1 -> Set(mod2)))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 1
        |  threshold: 5
        |  constraint_violated: false
        |  illegal_dependencies:
        |    - submodule: MOD1
        |      depends_on: [MOD2]
        |  missing_dependencies: []
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def multipleIllegalDependencies_reportedAlphabetically(): Unit = {
    val modA = submodule("MOD_A")
    val modB = submodule("MOD_B")
    val modC = submodule("MOD_C")
    val result = SubmodulesBlueprintAnalysisResult(
      0,
      illegalDependencies = Map(modB -> Set(modC), modA -> Set(modB)))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """submodules_blueprint_result:
        |  description: Submodules Blueprint constraint
        |  violation_count: 2
        |  threshold: 0
        |  constraint_violated: true
        |  illegal_dependencies:
        |    - submodule: MOD_A
        |      depends_on: [MOD_B]
        |    - submodule: MOD_B
        |      depends_on: [MOD_C]
        |  missing_dependencies: []
        |""".stripMargin
    assertEquals(expected, report)
  }
}

