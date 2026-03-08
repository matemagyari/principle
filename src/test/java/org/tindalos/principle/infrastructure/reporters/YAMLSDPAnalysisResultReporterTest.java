package org.tindalos.principle.infrastructure.reporters;

import org.junit.Test;
import org.tindalos.principle.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.domain.analyzers.sdp.SDPViolation;
import org.tindalos.principle.domain.constraints.SDP;
import org.tindalos.principle.domain.core.packages.PackageMetrics;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.tindalos.principle.infrastructure.reporters.YamlAssertions.assertValidYaml;

/**
 * Tests for YAMLSDPAnalysisResultReporter verifying correct YAML output
 * for various SDP analysis scenarios.
 */
public class YAMLSDPAnalysisResultReporterTest {

    private final YAMLSDPAnalysisResultReporter reporter = new YAMLSDPAnalysisResultReporter();
    private final SDP sdp = new SDP(0);

    private PackageWithMetrics pkg(String name, float instability) {
        return new PackageWithMetrics() {
            public PackageReference reference() { return new PackageReference(name); }
            public PackageMetrics getMetrics() { return new PackageMetrics(0, 0, 0, instability, 0); }
            public java.util.Set<PackageReference> accumulatedDirectPackageReferences() { return java.util.Set.of(); }
        };
    }

    private SDPViolation violation(String dependerName, float dependerInstability,
                                   String dependeeName, float dependeeInstability) {
        return new SDPViolation(pkg(dependerName, dependerInstability), pkg(dependeeName, dependeeInstability));
    }

    @Test
    public void noViolations_reportsEmptyViolations() {
        var result = new SDPResult(List.of(), sdp);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sdp_result:
                  description: Stable Dependencies Principle constraint
                  violation_count: 0
                  threshold: 0
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void noViolations_withThreshold_reportsThreshold() {
        var result = new SDPResult(List.of(), new SDP(3));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sdp_result:
                  description: Stable Dependencies Principle constraint
                  violation_count: 0
                  threshold: 3
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleViolation_reportsDependerAndDependee() {
        var result = new SDPResult(
                List.of(violation("com.example.app", 0.8f, "com.example.domain", 0.9f)),
                sdp);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sdp_result:
                  description: Stable Dependencies Principle constraint
                  violation_count: 1
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - depender: com.example.app
                      depender_instability: 0.8
                      dependee: com.example.domain
                      dependee_instability: 0.9
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withinThreshold_constraintNotViolated() {
        var result = new SDPResult(
                List.of(violation("com.example.app", 0.8f, "com.example.domain", 0.9f)),
                new SDP(5));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sdp_result:
                  description: Stable Dependencies Principle constraint
                  violation_count: 1
                  threshold: 5
                  constraint_violated: false
                  violations:
                    - depender: com.example.app
                      depender_instability: 0.8
                      dependee: com.example.domain
                      dependee_instability: 0.9
                """;
        assertEquals(expected, report);
    }

    @Test
    public void multipleViolations_allReported() {
        var result = new SDPResult(List.of(
                violation("com.example.app",    0.8f, "com.example.domain", 0.9f),
                violation("com.example.domain", 0.5f, "com.example.core",   0.7f)),
                sdp);

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                sdp_result:
                  description: Stable Dependencies Principle constraint
                  violation_count: 2
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - depender: com.example.app
                      depender_instability: 0.8
                      dependee: com.example.domain
                      dependee_instability: 0.9
                    - depender: com.example.domain
                      depender_instability: 0.5
                      dependee: com.example.core
                      dependee_instability: 0.7
                """;
        assertEquals(expected, report);
    }
}

