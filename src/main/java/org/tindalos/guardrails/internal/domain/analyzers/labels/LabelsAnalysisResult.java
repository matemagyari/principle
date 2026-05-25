package org.tindalos.guardrails.internal.domain.analyzers.labels;

import java.util.List;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

/**
 * Represents the cumulative analysis result for all label groups.
 */
public record LabelsAnalysisResult(List<LabelGroupResult> groupResults) implements AnalysisResult {
    public LabelsAnalysisResult {
        groupResults = List.copyOf(groupResults);
    }

    @Override
    public boolean constraintViolated() {
        return groupResults.stream().anyMatch(LabelGroupResult::constraintViolated);
    }
}
