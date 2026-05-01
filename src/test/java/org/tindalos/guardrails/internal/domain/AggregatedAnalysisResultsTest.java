package org.tindalos.guardrails.internal.domain;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.layering.LayerReference;
import org.tindalos.guardrails.internal.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.guardrails.internal.domain.constraints.ADP;

/**
 * Tests typed access and aggregate state helpers on AggregatedAnalysisResults.
 */
public class AggregatedAnalysisResultsTest {

    @Test
    public void typedAccessor_returnsFirstMatchingSubtype() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var layerResult = new LayerViolationsResult(Collections.singletonList(new LayerReference("a", "b")), 0);
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, layerResult));

        assertTrue(results.adpResult().isPresent());
        assertSame(adpResult, results.adpResult().get());
        assertTrue(results.layerViolationsResult().isPresent());
        assertSame(layerResult, results.layerViolationsResult().get());
        assertFalse(results.sdpResult().isPresent());
    }

    @Test
    public void hasViolations_isTrueWhenAnyWrappedResultViolatesConstraint() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var layerResult = new LayerViolationsResult(Collections.singletonList(new LayerReference("a", "b")), 0);
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, layerResult));

        assertTrue(results.hasViolations());
    }

    @Test
    public void hasViolations_isFalseWhenAllWrappedResultsPass() {
        var adpResult = new ADPResult(Map.of(), new ADP(0));
        var layerResult = new LayerViolationsResult(java.util.List.of(), 0);
        var results = new AggregatedAnalysisResults(java.util.List.of(adpResult, layerResult));

        assertFalse(results.hasViolations());
    }
}