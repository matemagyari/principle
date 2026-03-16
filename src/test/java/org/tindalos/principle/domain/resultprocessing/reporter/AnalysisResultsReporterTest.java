package org.tindalos.principle.domain.resultprocessing.reporter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.tindalos.principle.domain.AnalysisResult;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.analyzers.layering.LayerReference;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.domain.analyzers.structure.GroupingResult;
import org.tindalos.principle.domain.analyzers.structure.SubgraphDecomposition;
import org.tindalos.principle.domain.constraints.ADP;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.infrastructure.reporters.YAMLADPAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLComponentDependencyAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLLayerAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLSAPAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLSDPAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLSubmodulesBlueprintAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLThirdPartyAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests for AnalysisResultsReporter.summary() verifying that
 * a single YAML output is produced combining all partial results,
 * with a top-level success flag and failure description.
 */
public class AnalysisResultsReporterTest {

    private final AnalysisResultsReporter reporter = new AnalysisResultsReporter(
            new YAMLADPAnalysisResultReporter(),
            new YAMLLayerAnalysisResultReporter(),
            new YAMLThirdPartyAnalysisResultReporter(),
            new YAMLSAPAnalysisResultReporter(),
            new YAMLComponentDependencyAnalysisResultReporter(),
            new YAMLSubmodulesBlueprintAnalysisResultReporter(),
            new YAMLSDPAnalysisResultReporter(),
            new YAMLPackageCohesionAnalysisResultReporter()
    );

    private PackageReference ref(String name) {
        return new PackageReference(name);
    }

    private List<AnalysisResult> javaList(AnalysisResult... results) {
      return java.util.Arrays.asList(results);
    }

    private void assertValidYaml(String yaml) {
        var parsed = new Yaml().load(yaml);
        assertNotNull("YAML must parse to a non-null object", parsed);
    }

    @Test
    public void noResults_returnsSuccessWithAllSatisfied() {
        var result = reporter.summary(javaList());

        assertValidYaml(result);

        var expected = """
                analysis_summary:
                  success: true
                  description: "All constraints satisfied"
                  results: {}
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void singleSatisfiedResult_returnsSuccessAndIncludesResultSection() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));

        var result = reporter.summary(javaList(adpResult));

        assertValidYaml(result);

        var expected = """
                analysis_summary:
                  success: true
                  description: "All constraints satisfied"
                  results:
                    adp_result:
                      description: Acyclic Package Dependency Principle constraint
                      violation_count: 0
                      threshold: 0
                      constraint_violated: false
                      breaking_points: []
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void singleViolatedResult_returnsFailureWithViolatedName() {
        var cycle = new Cycle(ref("com.example.a"), ref("com.example.b"));
        var adpResult = new ADPResult(Map.of(ref("com.example.a"), Collections.singleton(cycle)), new ADP(0));

        var result = reporter.summary(javaList(adpResult));

        assertValidYaml(result);

        var expected = """
                analysis_summary:
                  success: false
                  description: "Constraints violated in: adp_result"
                  results:
                    adp_result:
                      description: Acyclic Package Dependency Principle constraint
                      violation_count: 1
                      threshold: 0
                      constraint_violated: true
                      breaking_points:
                        - package: com.example.a
                          cycle_count: 1
                          cycles:
                            - [com.example.a, com.example.b]
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void multipleResults_someViolated_listsViolatedNamesInDescription() {
        var cycle = new Cycle(ref("com.a"), ref("com.b"));
        var adpResult = new ADPResult(Map.of(ref("com.a"), Collections.singleton(cycle)), new ADP(0));
        var layerResult = new LayerViolationsResult(
                Collections.singletonList(new LayerReference("com.a.Foo", "com.b.Bar")), 0);

        var result = reporter.summary(javaList(adpResult, layerResult));

        assertValidYaml(result);

        var expected = """
                analysis_summary:
                  success: false
                  description: "Constraints violated in: adp_result, layer_result"
                  results:
                    adp_result:
                      description: Acyclic Package Dependency Principle constraint
                      violation_count: 1
                      threshold: 0
                      constraint_violated: true
                      breaking_points:
                        - package: com.a
                          cycle_count: 1
                          cycles:
                            - [com.a, com.b]
                    layer_result:
                      description: Layering constraint
                      violation_count: 1
                      threshold: 0
                      constraint_violated: true
                      violations:
                        - referrer: com.a.Foo
                          referee: com.b.Bar
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void multipleResults_nonViolated_returnsSuccess() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var layerResult = new LayerViolationsResult(java.util.List.of(), 0);

        var result = reporter.summary(javaList(adpResult, layerResult));

        assertValidYaml(result);

        var expected = """
                analysis_summary:
                  success: true
                  description: "All constraints satisfied"
                  results:
                    adp_result:
                      description: Acyclic Package Dependency Principle constraint
                      violation_count: 0
                      threshold: 0
                      constraint_violated: false
                      breaking_points: []
                    layer_result:
                      description: Layering constraint
                      violation_count: 0
                      threshold: 0
                      constraint_violated: false
                      violations: []
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void cohesionResult_includesCohesionSectionInSummary() {
        new java.io.File("./principle_reports").mkdirs();
        var cohesionResult = new CohesionAnalysisResult(
                java.util.Collections.emptyMap(),
                java.util.Optional.empty(),
                new GroupingResult(java.util.Collections.emptyMap(), java.util.Collections.emptyList()),
                new SubgraphDecomposition(java.util.Collections.emptyList())
        );

        var result = reporter.summary(javaList(cohesionResult));

        assertValidYaml(result);

        var expected = """
                analysis_summary:
                  success: true
                  description: "All constraints satisfied"
                  results:
                    package_cohesion_result:
                      description: Package Cohesion Analysis
                      package_count: 0
                      detail_files:
                        - existing_packages_cohesion.txt
                        - code_structure_observations1.txt
                        - code_structure_observations2.txt
                      packages: []
                """.stripIndent();
        assertEquals(expected, result);
    }
}
