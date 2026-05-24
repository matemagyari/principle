package org.tindalos.guardrails.internal.domain.analyzers.slices;

import java.util.List;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

/**
 * Represents the cumulative analysis result for all slice groups.
 */
public record SlicesAnalysisResult(List<SliceGroupResult> groupResults) implements AnalysisResult {
    public SlicesAnalysisResult {
        groupResults = List.copyOf(groupResults);
    }

    @Override
    public boolean constraintViolated() {
        return groupResults.stream().anyMatch(SliceGroupResult::constraintViolated);
    }
}
