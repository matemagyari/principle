package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import org.tindalos.principle.domain.AnalysisResult;

import java.util.Map;
import java.util.Set;

/**
 * Represents the result of a Submodules Blueprint analysis.
 * Contains illegal dependencies, missing dependencies, and overlapping module definitions.
 *
 * @param violationThreshold maximum number of violations allowed
 * @param illegalDependencies map of submodules to their illegal dependencies
 * @param missingDependencies map of submodules to their missing planned dependencies
 * @param overlaps set of overlapping module definitions, if any
 */
public record SubmodulesBlueprintAnalysisResult(
        int violationThreshold,
        Map<Submodule, Set<Submodule>> illegalDependencies,
        Map<Submodule, Set<Submodule>> missingDependencies,
        Set<Overlap> overlaps) implements AnalysisResult {

    public SubmodulesBlueprintAnalysisResult {
        illegalDependencies = Map.copyOf(illegalDependencies);
        missingDependencies = Map.copyOf(missingDependencies);
        overlaps = Set.copyOf(overlaps);
    }

    public static SubmodulesBlueprintAnalysisResult empty(int violationThreshold) {
        return new SubmodulesBlueprintAnalysisResult(violationThreshold, Map.of(), Map.of(), Set.of());
    }

    public static SubmodulesBlueprintAnalysisResult withViolations(int violationThreshold,
                                                                    Map<Submodule, Set<Submodule>> illegalDependencies,
                                                                    Map<Submodule, Set<Submodule>> missingDependencies) {
        return new SubmodulesBlueprintAnalysisResult(violationThreshold, illegalDependencies, missingDependencies, Set.of());
    }

    public static SubmodulesBlueprintAnalysisResult withOverlaps(int violationThreshold, Set<Overlap> overlaps) {
        return new SubmodulesBlueprintAnalysisResult(violationThreshold, Map.of(), Map.of(), overlaps);
    }

    public int threshold() {
        return violationThreshold;
    }

    public int violationsNumber() {
        return illegalDependencies.size() + missingDependencies.size();
    }

    @Override
    public boolean constraintViolated() {
        return violationsNumber() > threshold();
    }
}

