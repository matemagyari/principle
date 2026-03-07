package org.tindalos.principle.infrastructure.reporters;

import org.junit.Test;
import org.tindalos.principle.domain.analyzers.layering.LayerReference;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.tindalos.principle.infrastructure.reporters.YamlAssertions.assertValidYaml;

/**
 * Tests for YAMLLayerAnalysisResultReporter verifying correct YAML output
 * for various layering analysis scenarios.
 */
public class YAMLLayerAnalysisResultReporterTest {

    private final YAMLLayerAnalysisResultReporter reporter = new YAMLLayerAnalysisResultReporter();


    @Test
    public void noViolations_reportsEmptyViolations() {
        var result = new LayerViolationsResult(List.of(), 0);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                layer_result:
                  description: Layering constraint
                  violation_count: 0
                  threshold: 0
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void noViolations_withThreshold_reportsThreshold() {
        var result = new LayerViolationsResult(List.of(), 3);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                layer_result:
                  description: Layering constraint
                  violation_count: 0
                  threshold: 3
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleViolation_reportsReferrerAndReferee() {
        var result = new LayerViolationsResult(
                List.of(new LayerReference("com.example.domain", "com.example.infrastructure")), 0);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                layer_result:
                  description: Layering constraint
                  violation_count: 1
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - referrer: com.example.domain
                      referee: com.example.infrastructure
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withinThreshold_constraintNotViolated() {
        var result = new LayerViolationsResult(
                List.of(new LayerReference("com.example.domain", "com.example.infrastructure")), 5);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                layer_result:
                  description: Layering constraint
                  violation_count: 1
                  threshold: 5
                  constraint_violated: false
                  violations:
                    - referrer: com.example.domain
                      referee: com.example.infrastructure
                """;
        assertEquals(expected, report);
    }

    @Test
    public void multipleViolations_allReported() {
        var result = new LayerViolationsResult(List.of(
                new LayerReference("com.example.domain", "com.example.app"),
                new LayerReference("com.example.domain", "com.example.infrastructure"),
                new LayerReference("com.example.app", "com.example.infrastructure")), 0);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                layer_result:
                  description: Layering constraint
                  violation_count: 3
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - referrer: com.example.domain
                      referee: com.example.app
                    - referrer: com.example.domain
                      referee: com.example.infrastructure
                    - referrer: com.example.app
                      referee: com.example.infrastructure
                """;
        assertEquals(expected, report);
    }
}

