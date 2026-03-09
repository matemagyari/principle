package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.analyzers.structure.Graph.{Node, SubgraphDecomposition}
import org.tindalos.principle.domain.analyzers.structure.PackageStructureHints1Finder.GroupingResult
import org.tindalos.principle.domain.analyzers.structure.Structure.NodeGroup
import org.yaml.snakeyaml.Yaml

class YAMLPackageCohesionAnalysisResultReporterTest {

  private val reporter = new YAMLPackageCohesionAnalysisResultReporter()

  private def assertValidYaml(yaml: String): Unit = {
    val parsed = new Yaml().load(yaml)
    assert(parsed != null, "YAML must parse to a non-null object")
  }

  private val emptyResult = CohesionAnalysisResult(
    packages = Set.empty,
    cohesiveNodeGroups = None,
    groupingResult = GroupingResult(Map.empty, List.empty),
    subgraphDecomposition = SubgraphDecomposition(List.empty)
  )

  private def pkg(packageName: String, nodeIds: String*): (String, NodeGroup) = {
    val nodes = nodeIds.map(id => Node(id, Set.empty, Set.empty)).toSet
    (packageName, NodeGroup(nodes))
  }

  @Test
  def noPackages_reportsEmptyPackageList(): Unit = {
    new java.io.File("./principle_reports").mkdirs()
    val report = reporter.report(emptyResult)

    assertValidYaml(report)

    val expected =
      """package_cohesion_result:
        |  description: Package Cohesion Analysis
        |  package_count: 0
        |  detail_files:
        |    - existing_packages_cohesion.txt
        |    - code_structure_observations1.txt
        |    - code_structure_observations2.txt
        |  packages: []
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def singlePackage_reportsNameCohesionAndSize(): Unit = {
    new java.io.File("./principle_reports").mkdirs()
    val result = emptyResult.copy(packages = Set(pkg("org.example.domain", "org.example.domain.Foo", "org.example.domain.Bar")))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """package_cohesion_result:
        |  description: Package Cohesion Analysis
        |  package_count: 1
        |  detail_files:
        |    - existing_packages_cohesion.txt
        |    - code_structure_observations1.txt
        |    - code_structure_observations2.txt
        |  packages:
        |    - name: org.example.domain
        |      cohesion: 0.0
        |      size: 2
        |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def multiplePackages_sortedAlphabeticallyByName(): Unit = {
    new java.io.File("./principle_reports").mkdirs()
    val result = emptyResult.copy(packages = Set(
      pkg("org.example.infrastructure", "org.example.infrastructure.Repo"),
      pkg("org.example.app", "org.example.app.Service"),
      pkg("org.example.domain", "org.example.domain.Entity")
    ))

    val report = reporter.report(result)

    assertValidYaml(report)

    val expected =
      """package_cohesion_result:
        |  description: Package Cohesion Analysis
        |  package_count: 3
        |  detail_files:
        |    - existing_packages_cohesion.txt
        |    - code_structure_observations1.txt
        |    - code_structure_observations2.txt
        |  packages:
        |    - name: org.example.app
        |      cohesion: 0.0
        |      size: 1
        |    - name: org.example.domain
        |      cohesion: 0.0
        |      size: 1
        |    - name: org.example.infrastructure
        |      cohesion: 0.0
        |      size: 1
        |""".stripMargin
    assertEquals(expected, report)
  }
}
