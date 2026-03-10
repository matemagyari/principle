package org.tindalos.principle.domain.resultprocessing.reporter

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.adp.ADPResult
import org.tindalos.principle.domain.analyzers.layering.{LayerReference, LayerViolationsResult}
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.analyzers.structure.SubgraphDecomposition
import org.tindalos.principle.domain.analyzers.structure.GroupingResult
import org.tindalos.principle.domain.constraints.ADP
import org.tindalos.principle.domain.core.Cycle
import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter
import org.yaml.snakeyaml.Yaml

import java.util.Collections
import java.util.{List => JList, Map => JMap, Set => JSet}

/**
 * Tests for AnalysisResultsReporter.summary() verifying that
 * a single YAML output is produced combining all partial results,
 * with a top-level success flag and failure description.
 */
class AnalysisResultsReporterTest {

  private val reporter = new AnalysisResultsReporter(
    new YAMLADPAnalysisResultReporter(),
    new YAMLLayerAnalysisResultReporter(),
    new YAMLThirdPartyAnalysisResultReporter(),
    new YAMLSAPAnalysisResultReporter(),
    new YAMLComponentDependencyAnalysisResultReporter(),
    new YAMLSubmodulesBlueprintAnalysisResultReporter(),
    new YAMLSDPAnalysisResultReporter(),
    new YAMLPackageCohesionAnalysisResultReporter()
  )

  private def ref(name: String) = new PackageReference(name)

  private def assertValidYaml(yaml: String): Unit = {
    val parsed = new Yaml().load(yaml)
    assert(parsed != null, "YAML must parse to a non-null object")
  }

  @Test
  def noResults_returnsSuccessWithAllSatisfied(): Unit = {
    val result = reporter.summary(List.empty)

    assertValidYaml(result)

    val expected =
      """analysis_summary:
        |  success: true
        |  description: "All constraints satisfied"
        |  results: {}
        |""".stripMargin
    assertEquals(expected, result)
  }

  @Test
  def singleSatisfiedResult_returnsSuccessAndIncludesResultSection(): Unit = {
    val adpResult = new ADPResult(JMap.of(), new ADP(0))

    val result = reporter.summary(List(adpResult))

    assertValidYaml(result)

    val expected =
      """analysis_summary:
        |  success: true
        |  description: "All constraints satisfied"
        |  results:
        |    adp_result:
        |      description: Acyclic Package Dependency Principle constraint
        |      violation_count: 0
        |      threshold: 0
        |      constraint_violated: false
        |      breaking_points: []
        |""".stripMargin
    assertEquals(expected, result)
  }

  @Test
  def singleViolatedResult_returnsFailureWithViolatedName(): Unit = {
    val cycle = new Cycle(ref("com.example.a"), ref("com.example.b"))
    val adpResult = new ADPResult(JMap.of(ref("com.example.a"), Collections.singleton(cycle)), new ADP(0))

    val result = reporter.summary(List(adpResult))

    assertValidYaml(result)

    val expected =
      """analysis_summary:
        |  success: false
        |  description: "Constraints violated in: adp_result"
        |  results:
        |    adp_result:
        |      description: Acyclic Package Dependency Principle constraint
        |      violation_count: 1
        |      threshold: 0
        |      constraint_violated: true
        |      breaking_points:
        |        - package: com.example.a
        |          cycle_count: 1
        |          cycles:
        |            - [com.example.a, com.example.b]
        |""".stripMargin
    assertEquals(expected, result)
  }

  @Test
  def multipleResults_someViolated_listsViolatedNamesInDescription(): Unit = {
    val cycle = new Cycle(ref("com.a"), ref("com.b"))
    val adpResult = new ADPResult(JMap.of(ref("com.a"), Collections.singleton(cycle)), new ADP(0))
    val layerResult = new LayerViolationsResult(
      Collections.singletonList(new LayerReference("com.a.Foo", "com.b.Bar")), 0)

    val result = reporter.summary(List(adpResult, layerResult))

    assertValidYaml(result)

    val expected =
      """analysis_summary:
        |  success: false
        |  description: "Constraints violated in: adp_result, layer_result"
        |  results:
        |    adp_result:
        |      description: Acyclic Package Dependency Principle constraint
        |      violation_count: 1
        |      threshold: 0
        |      constraint_violated: true
        |      breaking_points:
        |        - package: com.a
        |          cycle_count: 1
        |          cycles:
        |            - [com.a, com.b]
        |    layer_result:
        |      description: Layering constraint
        |      violation_count: 1
        |      threshold: 0
        |      constraint_violated: true
        |      violations:
        |        - referrer: com.a.Foo
        |          referee: com.b.Bar
        |""".stripMargin
    assertEquals(expected, result)
  }

  @Test
  def multipleResults_nonViolated_returnsSuccess(): Unit = {
    val adpResult = new ADPResult(JMap.of(), new ADP(0))
    val layerResult = new LayerViolationsResult(JList.of(), 0)

    val result = reporter.summary(List(adpResult, layerResult))

    assertValidYaml(result)

    val expected =
      """analysis_summary:
        |  success: true
        |  description: "All constraints satisfied"
        |  results:
        |    adp_result:
        |      description: Acyclic Package Dependency Principle constraint
        |      violation_count: 0
        |      threshold: 0
        |      constraint_violated: false
        |      breaking_points: []
        |    layer_result:
        |      description: Layering constraint
        |      violation_count: 0
        |      threshold: 0
        |      constraint_violated: false
        |      violations: []
        |""".stripMargin
    assertEquals(expected, result)
  }

  @Test
  def cohesionResult_includesCohesionSectionInSummary(): Unit = {
    new java.io.File("./principle_reports").mkdirs()
    val cohesionResult = CohesionAnalysisResult(
      packages = Set.empty,
      cohesiveNodeGroups = None,
      groupingResult = new GroupingResult(java.util.Collections.emptyMap(), java.util.Collections.emptyList()),
      subgraphDecomposition = new SubgraphDecomposition(java.util.Collections.emptyList())
    )

    val result = reporter.summary(List(cohesionResult))

    assertValidYaml(result)

    val expected =
      """analysis_summary:
        |  success: true
        |  description: "All constraints satisfied"
        |  results:
        |    package_cohesion_result:
        |      description: Package Cohesion Analysis
        |      package_count: 0
        |      detail_files:
        |        - existing_packages_cohesion.txt
        |        - code_structure_observations1.txt
        |        - code_structure_observations2.txt
        |      packages: []
        |""".stripMargin
    assertEquals(expected, result)
  }
}
