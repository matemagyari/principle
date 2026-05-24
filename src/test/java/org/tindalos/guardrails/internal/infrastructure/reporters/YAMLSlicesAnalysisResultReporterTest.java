package org.tindalos.guardrails.internal.infrastructure.reporters;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.slices.Slice;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SliceGroupResult;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SlicesAnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceOverlap;
import static org.tindalos.guardrails.internal.infrastructure.reporters.YamlAssertions.assertValidYaml;

public class YAMLSlicesAnalysisResultReporterTest {

    private final YAMLSlicesAnalysisResultReporter reporter = new YAMLSlicesAnalysisResultReporter();

    @Test
    public void resultType_isSlicesAnalysisResult() {
        assertEquals(SlicesAnalysisResult.class, reporter.resultType());
    }

    @Test
    public void emptyResult_reportsCorrectly() {
        var result = new SlicesAnalysisResult(List.of());
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                slices_result:
                  description: Slices constraints
                  constraint_violated: false
                  groups: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleGroupNoViolations_reportsCorrectly() {
        var groupResult = SliceGroupResult.empty("layers", 0);
        var result = new SlicesAnalysisResult(List.of(groupResult));
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
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
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withIllegalAndMissingDependencies_reportsCorrectly() {
        var infraSlice = new Slice(new SliceId("infra"), Set.of(), Set.of());
        var appSlice = new Slice(new SliceId("app"), Set.of(), Set.of());
        var domainSlice = new Slice(new SliceId("domain"), Set.of(), Set.of());

        Map<Slice, Set<Slice>> illegal = Map.of(infraSlice, Set.of(domainSlice));
        Map<Slice, Set<Slice>> missing = Map.of(infraSlice, Set.of(appSlice));

        var groupResult = new SliceGroupResult("layers", 0, illegal, missing, Set.of());
        var result = new SlicesAnalysisResult(List.of(groupResult));
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                slices_result:
                  description: Slices constraints
                  constraint_violated: true
                  groups:
                    - name: layers
                      violation_count: 2
                      threshold: 0
                      constraint_violated: true
                      illegal_dependencies:
                        - slice: infra
                          depends_on: [domain]
                      missing_dependencies:
                        - slice: infra
                          depends_on: [app]
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withOverlaps_reportsCorrectly() {
        var overlap = new SliceOverlap(new SliceId("infra"), new SliceId("app"));
        var groupResult = new SliceGroupResult("layers", 0, Map.of(), Map.of(), Set.of(overlap));
        var result = new SlicesAnalysisResult(List.of(groupResult));
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                slices_result:
                  description: Slices constraints
                  constraint_violated: true
                  groups:
                    - name: layers
                      violation_count: 0
                      threshold: 0
                      constraint_violated: true
                      overlaps: true
                """;
        assertEquals(expected, report);
    }
}