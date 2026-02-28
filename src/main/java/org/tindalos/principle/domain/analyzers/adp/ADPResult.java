package org.tindalos.principle.domain.analyzers.adp;

import org.tindalos.principle.domain.AnalysisResult;
import org.tindalos.principle.domain.constraints.ADP;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.packages.PackageReference;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the result of an Acyclic Dependency Principle (ADP) analysis.
 * Contains detected dependency cycles grouped by the package that could break each cycle.
 *
 * @param cyclesByBreakingPoints immutable map of package references to the cycles they are involved in
 * @param expectation            the ADP constraint configuration
 */
public record ADPResult(
        Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints,
        ADP expectation) implements AnalysisResult {

    public ADPResult {
        cyclesByBreakingPoints = cyclesByBreakingPoints.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> Set.copyOf(e.getValue())));
    }

    public int threshold() {
        return expectation.violationThreshold();
    }

    @Override
    public boolean constraintViolated() {
        return cyclesByBreakingPoints.size() > threshold();
    }
}

