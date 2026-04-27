package org.tindalos.principle.internal.infrastructure.reporters;

import org.junit.Test;
import org.tindalos.principle.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.internal.domain.constraints.ADP;
import org.tindalos.principle.internal.domain.core.Cycle;
import org.tindalos.principle.internal.domain.core.packages.PackageReference;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.tindalos.principle.internal.infrastructure.reporters.YamlAssertions.assertValidYaml;

/**
 * Tests for YAMLADPAnalysisResultReporter verifying correct YAML output
 * for various ADP analysis scenarios.
 */
public class YAMLADPAnalysisResultReporterTest {

    private final YAMLADPAnalysisResultReporter reporter = new YAMLADPAnalysisResultReporter();

    private PackageReference ref(String name) {
        return new PackageReference(name);
    }


    @Test
    public void noViolations_reportsEmptyBreakingPoints() {
        var result = new ADPResult(Map.of(), new ADP(0));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                adp_result:
                  description: Acyclic Package Dependency Principle constraint
                  violation_count: 0
                  threshold: 0
                  constraint_violated: false
                  breaking_points: []
                """.stripIndent();
        assertEquals(expected, report);
    }

    @Test
    public void noViolations_withThreshold_reportsThreshold() {
        var result = new ADPResult(Map.of(), new ADP(5));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                adp_result:
                  description: Acyclic Package Dependency Principle constraint
                  violation_count: 0
                  threshold: 5
                  constraint_violated: false
                  breaking_points: []
                """.stripIndent();
        assertEquals(expected, report);
    }

    @Test
    public void singleViolation_reportsBreakingPointAndCycle() {
        var cycle = new Cycle(ref("com.example.a"), ref("com.example.b"));
        var result = new ADPResult(
                Map.of(ref("com.example.a"), Set.of(cycle)),
                new ADP(0));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
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
        assertEquals(expected, report);
    }

    @Test
    public void multipleBreakingPoints_reportedAlphabetically() {
        var cycleA = new Cycle(ref("com.example.a"), ref("com.example.b"));
        var cycleC = new Cycle(ref("com.example.c"), ref("com.example.d"));
        var result = new ADPResult(
                Map.of(
                        ref("com.example.c"), Set.of(cycleC),
                        ref("com.example.a"), Set.of(cycleA)),
                new ADP(0));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                adp_result:
                  description: Acyclic Package Dependency Principle constraint
                  violation_count: 2
                  threshold: 0
                  constraint_violated: true
                  breaking_points:
                    - package: com.example.a
                      cycle_count: 1
                      cycles:
                        - [com.example.a, com.example.b]
                    - package: com.example.c
                      cycle_count: 1
                      cycles:
                        - [com.example.c, com.example.d]
                """.stripIndent();
        assertEquals(expected, report);
    }

    @Test
    public void withinThreshold_constraintNotViolated() {
        var cycle = new Cycle(ref("com.example.a"), ref("com.example.b"));
        var result = new ADPResult(
                Map.of(ref("com.example.a"), Set.of(cycle)),
                new ADP(5));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                adp_result:
                  description: Acyclic Package Dependency Principle constraint
                  violation_count: 1
                  threshold: 5
                  constraint_violated: false
                  breaking_points:
                    - package: com.example.a
                      cycle_count: 1
                      cycles:
                        - [com.example.a, com.example.b]
                """.stripIndent();
        assertEquals(expected, report);
    }
}
