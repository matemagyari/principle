package org.tindalos.guardrails.internal.infrastructure.reporters;

import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.submodules.Overlap;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.Submodule;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleId;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for YAMLSubmodulesBlueprintAnalysisResultReporter verifying correct YAML output
 * for various submodules blueprint analysis scenarios.
 */
public class YAMLSubmodulesBlueprintAnalysisResultReporterTest {

    private final YAMLSubmodulesBlueprintAnalysisResultReporter reporter = new YAMLSubmodulesBlueprintAnalysisResultReporter();

    private Submodule submodule(String id) {
        return new Submodule(new SubmoduleId(id), Set.of(), Set.of());
    }

    private void assertValidYaml(String yaml) {
        var parsed = new Yaml().load(yaml);
        assertNotNull(parsed, "YAML must parse to a non-null object");
    }

    @Test
    public void noViolations_reportsEmptyDependencies() {
        var result = SubmodulesBlueprintAnalysisResult.empty(0);

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 0
                  threshold: 0
                  constraint_violated: false
                  illegal_dependencies: []
                  missing_dependencies: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void noViolations_withThreshold_reportsThreshold() {
        var result = SubmodulesBlueprintAnalysisResult.empty(3);

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 0
                  threshold: 3
                  constraint_violated: false
                  illegal_dependencies: []
                  missing_dependencies: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withOverlap_reportsOverlap() {
        var overlap = new Overlap(new SubmoduleId("MOD1"), new SubmoduleId("MOD2"));
        var result = SubmodulesBlueprintAnalysisResult.withOverlaps(0, Set.of(overlap));

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 0
                  threshold: 0
                  constraint_violated: false
                  overlaps: true
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withIllegalDependency_reportsIllegalDependency() {
        var mod1 = submodule("MOD1");
        var mod2 = submodule("MOD2");
        var result = SubmodulesBlueprintAnalysisResult.withViolations(0, Map.of(mod1, Set.of(mod2)), Map.of());

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 1
                  threshold: 0
                  constraint_violated: true
                  illegal_dependencies:
                    - submodule: MOD1
                      depends_on: [MOD2]
                  missing_dependencies: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withMissingDependency_reportsMissingDependency() {
        var mod1 = submodule("MOD1");
        var mod2 = submodule("MOD2");
        var result = SubmodulesBlueprintAnalysisResult.withViolations(0, Map.of(), Map.of(mod1, Set.of(mod2)));

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 1
                  threshold: 0
                  constraint_violated: true
                  illegal_dependencies: []
                  missing_dependencies:
                    - submodule: MOD1
                      depends_on: [MOD2]
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withIllegalAndMissingDependencies_reportsBoth() {
        var mod1 = submodule("MOD1");
        var mod2 = submodule("MOD2");
        var mod3 = submodule("MOD3");
        var result = SubmodulesBlueprintAnalysisResult.withViolations(
                0,
                Map.of(mod1, Set.of(mod2)),
                Map.of(mod1, Set.of(mod3)));

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 2
                  threshold: 0
                  constraint_violated: true
                  illegal_dependencies:
                    - submodule: MOD1
                      depends_on: [MOD2]
                  missing_dependencies:
                    - submodule: MOD1
                      depends_on: [MOD3]
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withinThreshold_constraintNotViolated() {
        var mod1 = submodule("MOD1");
        var mod2 = submodule("MOD2");
        var result = SubmodulesBlueprintAnalysisResult.withViolations(5, Map.of(mod1, Set.of(mod2)), Map.of());

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 1
                  threshold: 5
                  constraint_violated: false
                  illegal_dependencies:
                    - submodule: MOD1
                      depends_on: [MOD2]
                  missing_dependencies: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void multipleIllegalDependencies_reportedAlphabetically() {
        var modA = submodule("MOD_A");
        var modB = submodule("MOD_B");
        var modC = submodule("MOD_C");
        var result = SubmodulesBlueprintAnalysisResult.withViolations(
                0,
                Map.of(modB, Set.of(modC), modA, Set.of(modB)),
                Map.of());

        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: 2
                  threshold: 0
                  constraint_violated: true
                  illegal_dependencies:
                    - submodule: MOD_A
                      depends_on: [MOD_B]
                    - submodule: MOD_B
                      depends_on: [MOD_C]
                  missing_dependencies: []
                """;
        assertEquals(expected, report);
    }
}
