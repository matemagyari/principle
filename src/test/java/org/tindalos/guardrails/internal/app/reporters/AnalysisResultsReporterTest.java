package org.tindalos.guardrails.internal.app.reporters;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SlicesAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SliceGroupResult;
import org.tindalos.guardrails.internal.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.structure.GroupingResult;
import org.tindalos.guardrails.internal.domain.analyzers.structure.SubgraphDecomposition;
import org.tindalos.guardrails.internal.domain.constraints.ADP;
import org.tindalos.guardrails.internal.domain.core.AnalysisResult;
import org.tindalos.guardrails.internal.domain.core.Cycle;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLADPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLComponentDependencyAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSAPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSDPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSlicesAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLThirdPartyAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests for AnalysisResultsReporter.summary() verifying that
 * a single YAML output is produced combining all partial results,
 * with a top-level success flag and failure description.
 */
public class AnalysisResultsReporterTest {

    private final AnalysisResultsReporter reporter = new AnalysisResultsReporter(List.of(
            new YAMLADPAnalysisResultReporter(),
            new YAMLThirdPartyAnalysisResultReporter(),
            new YAMLSAPAnalysisResultReporter(),
            new YAMLComponentDependencyAnalysisResultReporter(),
            new YAMLSlicesAnalysisResultReporter(),
            new YAMLSDPAnalysisResultReporter(),
            new YAMLPackageCohesionAnalysisResultReporter())
    );

    private PackageReference ref(String name) {
        return new PackageReference(name);
    }

    private AggregatedAnalysisResults aggregatedResults(AnalysisResult... results) {
      return new AggregatedAnalysisResults(List.of(results));
    }

    private void assertValidYaml(String yaml) {
        var parsed = new Yaml().load(yaml);
        assertNotNull(parsed, "YAML must parse to a non-null object");
    }

    @Test
    public void noResults_returnsSuccessWithAllSatisfied() {
        var result = reporter.summary(aggregatedResults());

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

        var result = reporter.summary(aggregatedResults(adpResult));

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

        var result = reporter.summary(aggregatedResults(adpResult));

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
        var slicesResult = new SlicesAnalysisResult(List.of(
                SliceGroupResult.empty("layers", 0)
        ));

        var result = reporter.summary(aggregatedResults(adpResult, slicesResult));

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
                        - package: com.a
                          cycle_count: 1
                          cycles:
                            - [com.a, com.b]
                    slices_result:
                      description: Slices constraints
                      constraint_violated: false
                      groups:
                        - name: layers
                          violation_count: 0
                          threshold: 0
                          constraint_violated: false
                          illegal_dependencies: []
                          missing_dependencies: []
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void multipleResults_nonViolated_returnsSuccess() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var slicesResult = new SlicesAnalysisResult(List.of());

        var result = reporter.summary(aggregatedResults(adpResult, slicesResult));

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
                    slices_result:
                      description: Slices constraints
                      constraint_violated: false
                      groups: []
                """.stripIndent();
        assertEquals(expected, result);
    }

    @Test
    public void cohesionResult_includesCohesionSectionInSummary() {
        new java.io.File("./guardrails_reports").mkdirs();
        var cohesionResult = new CohesionAnalysisResult(
                java.util.Collections.emptyMap(),
                java.util.Optional.empty(),
                new GroupingResult(java.util.Collections.emptyMap(), java.util.Collections.emptyList()),
                new SubgraphDecomposition(java.util.Collections.emptyList())
        );

        var result = reporter.summary(aggregatedResults(cohesionResult));

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