package org.tindalos.guardrails.internal.domain.analyzers.labels;

import java.util.Map;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.constraints.labels.LabelOverlap;

/**
 * Represents the analysis result for a single label group.
 */
public record LabelGroupResult(
        String name,
        int violationThreshold,
        Map<Label, Set<Label>> illegalDependencies,
        Map<Label, Set<Label>> missingDependencies,
        Set<LabelOverlap> overlaps) {

    public LabelGroupResult {
        illegalDependencies = Map.copyOf(illegalDependencies);
        missingDependencies = Map.copyOf(missingDependencies);
        overlaps = Set.copyOf(overlaps);
    }

    public static LabelGroupResult empty(String name, int violationThreshold) {
        return new LabelGroupResult(name, violationThreshold, Map.of(), Map.of(), Set.of());
    }

    public int violationsNumber() {
        return illegalDependencies.size() + missingDependencies.size();
    }

    public boolean constraintViolated() {
        return !overlaps.isEmpty() || violationsNumber() > violationThreshold;
    }
}
