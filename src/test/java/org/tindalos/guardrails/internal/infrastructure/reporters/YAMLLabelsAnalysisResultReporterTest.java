package org.tindalos.guardrails.internal.infrastructure.reporters;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.labels.Label;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelGroupResult;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelsAnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelOverlap;
import static org.tindalos.guardrails.internal.infrastructure.reporters.YamlAssertions.assertValidYaml;

public class YAMLLabelsAnalysisResultReporterTest {

    private final YAMLLabelsAnalysisResultReporter reporter = new YAMLLabelsAnalysisResultReporter();

    @Test
    public void resultType_isLabelsAnalysisResult() {
        assertEquals(LabelsAnalysisResult.class, reporter.resultType());
    }

    @Test
    public void emptyResult_reportsCorrectly() {
        var result = new LabelsAnalysisResult(List.of());
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                labels_result:
                  description: Labels constraints
                  constraint_violated: false
                  groups: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleGroupNoViolations_reportsCorrectly() {
        var groupResult = LabelGroupResult.empty("layers", 0);
        var result = new LabelsAnalysisResult(List.of(groupResult));
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                labels_result:
                  description: Labels constraints
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
        var infraLabel = new Label(new LabelId("infra"), Set.of(), Set.of());
        var appLabel = new Label(new LabelId("app"), Set.of(), Set.of());
        var domainLabel = new Label(new LabelId("domain"), Set.of(), Set.of());

        Map<Label, Set<Label>> illegal = Map.of(infraLabel, Set.of(domainLabel));
        Map<Label, Set<Label>> missing = Map.of(infraLabel, Set.of(appLabel));

        var groupResult = new LabelGroupResult("layers", 0, illegal, missing, Set.of());
        var result = new LabelsAnalysisResult(List.of(groupResult));
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                labels_result:
                  description: Labels constraints
                  constraint_violated: true
                  groups:
                    - name: layers
                      violation_count: 2
                      threshold: 0
                      constraint_violated: true
                      illegal_dependencies:
                        - label: infra
                          depends_on: [domain]
                      missing_dependencies:
                        - label: infra
                          depends_on: [app]
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withOverlaps_reportsCorrectly() {
        var overlap = new LabelOverlap(new LabelId("infra"), new LabelId("app"));
        var groupResult = new LabelGroupResult("layers", 0, Map.of(), Map.of(), Set.of(overlap));
        var result = new LabelsAnalysisResult(List.of(groupResult));
        var report = reporter.report(result);

        assertValidYaml(report);
        var expected = """
                labels_result:
                  description: Labels constraints
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