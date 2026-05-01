package org.tindalos.guardrails.internal.infrastructure.reporters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPResult;
import org.tindalos.guardrails.internal.domain.constraints.SAP;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;
import static org.tindalos.guardrails.internal.infrastructure.reporters.YamlAssertions.assertValidYaml;

/**
 * Tests for YAMLSAPAnalysisResultReporter verifying correct YAML output
 * for various SAP analysis scenarios.
 */
public class YAMLSAPAnalysisResultReporterTest {

    private final YAMLSAPAnalysisResultReporter reporter = new YAMLSAPAnalysisResultReporter();
    private final SAP sap = new SAP(0, 0.25);

    private PackageWithMetrics pkg(String name, float distance) {
        return new PackageWithMetrics() {
            public PackageReference reference() { return new PackageReference(name); }
            public PackageMetrics getMetrics() { return new PackageMetrics(0, 0, 0, 0, distance); }
          public java.util.Set<PackageReference> getOwnPackageReferences() { return java.util.Set.of(); }
          public java.util.Set<PackageReference> getOwnExternalPackageReferences() { return java.util.Set.of(); }
            public java.util.Set<PackageReference> accumulatedDirectPackageReferences() { return java.util.Set.of(); }
        };
    }

    @Test
    public void noViolations_reportsEmptyViolations() {
        var result = new SAPResult(List.of(), sap);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sap_result:
                  description: Stable Abstractions Principle constraint
                  violation_count: 0
                  threshold: 0
                  max_distance: 0.25
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void noViolations_withThreshold_reportsThreshold() {
        var result = new SAPResult(List.of(), new SAP(3, 0.25));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sap_result:
                  description: Stable Abstractions Principle constraint
                  violation_count: 0
                  threshold: 3
                  max_distance: 0.25
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleViolation_reportsPackageAndDistance() {
        var result = new SAPResult(List.of(pkg("com.example.domain", 0.7f)), sap);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sap_result:
                  description: Stable Abstractions Principle constraint
                  violation_count: 1
                  threshold: 0
                  max_distance: 0.25
                  constraint_violated: true
                  violations:
                    - package: com.example.domain
                      distance: 0.7
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withinThreshold_constraintNotViolated() {
        var result = new SAPResult(List.of(pkg("com.example.domain", 0.7f)), new SAP(5, 0.25));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sap_result:
                  description: Stable Abstractions Principle constraint
                  violation_count: 1
                  threshold: 5
                  max_distance: 0.25
                  constraint_violated: false
                  violations:
                    - package: com.example.domain
                      distance: 0.7
                """;
        assertEquals(expected, report);
    }

    @Test
    public void multipleViolations_allReported() {
        var result = new SAPResult(List.of(
                pkg("com.example.domain", 0.7f),
                pkg("com.example.app", 0.5f)), sap);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sap_result:
                  description: Stable Abstractions Principle constraint
                  violation_count: 2
                  threshold: 0
                  max_distance: 0.25
                  constraint_violated: true
                  violations:
                    - package: com.example.domain
                      distance: 0.7
                    - package: com.example.app
                      distance: 0.5
                """;
        assertEquals(expected, report);
    }
}
