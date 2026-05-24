package org.tindalos.guardrails.internal.domain;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SlicesAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SliceGroupResult;
import org.tindalos.guardrails.internal.domain.constraints.ADP;

/**
 * Tests typed access and aggregate state helpers on AggregatedAnalysisResults.
 */
public class AggregatedAnalysisResultsTest {

    @Test
    public void typedAccessor_returnsFirstMatchingSubtype() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var slicesResult = new SlicesAnalysisResult(List.of(SliceGroupResult.empty("layers", 0)));
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, slicesResult));

        assertTrue(results.adpResult().isPresent());
        assertSame(adpResult, results.adpResult().get());
        assertTrue(results.slicesAnalysisResult().isPresent());
        assertSame(slicesResult, results.slicesAnalysisResult().get());
        assertFalse(results.sdpResult().isPresent());
    }

    @Test
    public void hasViolations_isTrueWhenAnyWrappedResultViolatesConstraint() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        // Force violation by passing non-empty overlaps
        var violatedSlicesResult = new SlicesAnalysisResult(List.of(
            new SliceGroupResult("layers", 0, Map.of(), Map.of(), Set.of(new org.tindalos.guardrails.internal.domain.constraints.slices.SliceOverlap(
                new org.tindalos.guardrails.internal.domain.constraints.slices.SliceId("a"),
                new org.tindalos.guardrails.internal.domain.constraints.slices.SliceId("b")
            )))
        ));
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, violatedSlicesResult));

        assertTrue(results.hasViolations());
    }

    @Test
    public void hasViolations_isFalseWhenAllWrappedResultsPass() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var slicesResult = new SlicesAnalysisResult(java.util.List.of());
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, slicesResult));

        assertFalse(results.hasViolations());
    }
}