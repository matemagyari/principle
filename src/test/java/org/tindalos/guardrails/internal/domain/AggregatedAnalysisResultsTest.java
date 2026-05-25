package org.tindalos.guardrails.internal.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelGroupResult;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelsAnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.ADP;

/**
 * Tests typed access and aggregate state helpers on AggregatedAnalysisResults.
 */
public class AggregatedAnalysisResultsTest {

    @Test
    public void typedAccessor_returnsFirstMatchingSubtype() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var labelsResult = new LabelsAnalysisResult(List.of(LabelGroupResult.empty("layers", 0)));
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, labelsResult));

        assertTrue(results.adpResult().isPresent());
        assertSame(adpResult, results.adpResult().get());
        assertTrue(results.labelsAnalysisResult().isPresent());
        assertSame(labelsResult, results.labelsAnalysisResult().get());
        assertFalse(results.sdpResult().isPresent());
    }

    @Test
    public void hasViolations_isTrueWhenAnyWrappedResultViolatesConstraint() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        // Force violation by passing non-empty overlaps
        var violatedLabelsResult = new LabelsAnalysisResult(List.of(
            new LabelGroupResult("layers", 0, Map.of(), Map.of(), Set.of(new org.tindalos.guardrails.internal.domain.constraints.labels.LabelOverlap(
                new org.tindalos.guardrails.internal.domain.constraints.labels.LabelId("a"),
                new org.tindalos.guardrails.internal.domain.constraints.labels.LabelId("b")
            )))
        ));
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, violatedLabelsResult));

        assertTrue(results.hasViolations());
    }

    @Test
    public void hasViolations_isFalseWhenAllWrappedResultsPass() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var labelsResult = new LabelsAnalysisResult(java.util.List.of());
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, labelsResult));

        assertFalse(results.hasViolations());
    }
}