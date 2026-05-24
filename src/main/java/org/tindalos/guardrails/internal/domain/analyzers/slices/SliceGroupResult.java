package org.tindalos.guardrails.internal.domain.analyzers.slices;

import java.util.Map;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.constraints.slices.SliceOverlap;

/**
 * Represents the analysis result for a single slice group within Slices logic.
 */
public record SliceGroupResult(
        String name,
        int violationThreshold,
        Map<Slice, Set<Slice>> illegalDependencies,
        Map<Slice, Set<Slice>> missingDependencies,
        Set<SliceOverlap> overlaps) {

    public SliceGroupResult {
        illegalDependencies = Map.copyOf(illegalDependencies);
        missingDependencies = Map.copyOf(missingDependencies);
        overlaps = Set.copyOf(overlaps);
    }

    public static SliceGroupResult empty(String name, int violationThreshold) {
        return new SliceGroupResult(name, violationThreshold, Map.of(), Map.of(), Set.of());
    }

    public int violationsNumber() {
        return illegalDependencies.size() + missingDependencies.size();
    }

    public boolean constraintViolated() {
        return !overlaps.isEmpty() || violationsNumber() > violationThreshold;
    }
}
