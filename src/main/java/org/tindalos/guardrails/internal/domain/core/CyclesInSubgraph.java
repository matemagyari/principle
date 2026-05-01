package org.tindalos.guardrails.internal.domain.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

/**
 * Tracks cycle detection progress in a package subgraph.
 * Immutable data structure: stores cycles grouped by their breaking points 
 * and remembers already investigated packages. Returns new instances for 
 * mutations to maintain functional purity.
 */
public record CyclesInSubgraph(
    Set<Package> investigatedPackages,
    Map<PackageReference, Set<Cycle>> breakingPoints
) {
    public static final int LIMIT = 5;

    public CyclesInSubgraph {
        investigatedPackages = Set.copyOf(investigatedPackages);
        breakingPoints = breakingPoints.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> Set.copyOf(entry.getValue())));
    }

    /**
     * Returns a new CyclesInSubgraph with the cycle added.
     * If the cycle already exists, returns this instance unchanged.
     */
    public CyclesInSubgraph withAddedCycle(Cycle cycle) {
        boolean exists = breakingPoints.values().stream().anyMatch(cycles -> cycles.contains(cycle));
        if (exists) return this;

        var updated = new HashMap<>(breakingPoints);
        var cyclesForEndpoint = updated.get(cycle.end());
        if (cyclesForEndpoint != null) {
            var newCycles = new java.util.HashSet<Cycle>(cyclesForEndpoint);
            newCycles.add(cycle);
            updated.put(cycle.end(), Set.copyOf(newCycles));
        } else {
            updated.put(cycle.end(), Set.of(cycle));
        }

        return new CyclesInSubgraph(investigatedPackages, updated);
    }

    /**
     * Returns a new CyclesInSubgraph with the package marked as investigated.
     */
    public CyclesInSubgraph withInvestigatedPackage(Package aPackage) {
        var updated = new java.util.HashSet<Package>(investigatedPackages);
        updated.add(aPackage);
        return new CyclesInSubgraph(updated, breakingPoints);
    }

    /**
     * Returns a new CyclesInSubgraph merged with another, combining both 
     * cycles and investigated packages.
     */
    public CyclesInSubgraph mergedWith(CyclesInSubgraph that) {
        var mergedInvestigated = new java.util.HashSet<Package>(investigatedPackages);
        mergedInvestigated.addAll(that.investigatedPackages);

        var mergedBreakingPoints = new HashMap<>(breakingPoints);
        that.breakingPoints.forEach((key, cycles) ->
            mergedBreakingPoints.merge(key, cycles, (v1, v2) -> {
                var combined = new java.util.HashSet<Cycle>(v1);
                combined.addAll(v2);
                return combined;
            }));

        return new CyclesInSubgraph(mergedInvestigated, mergedBreakingPoints);
    }

    /**
     * Merges cycles from another map into this subgraph and returns a map of all cycles.
     * This is used during cycle detection accumulation.
     */
    public Map<PackageReference, Set<Cycle>> mergeBreakingPoints2(Map<PackageReference, Set<Cycle>> breakingPointsInOther) {
        var accumulated = new CyclesInSubgraph(investigatedPackages, breakingPoints);
        var temp = new CyclesInSubgraph(Set.of(), breakingPointsInOther);
        var merged = accumulated.mergedWith(temp);
        return merged.cycles();
    }

    public Map<PackageReference, Set<Cycle>> cycles() {
        return breakingPoints;
    }

    public boolean isBreakingPoint(Package aPackage) {
        Set<Cycle> cyclesForThisBreakingPoint = breakingPoints.get(aPackage.reference());
        return cyclesForThisBreakingPoint != null && cyclesForThisBreakingPoint.size() > LIMIT;
    }

    @Override
    public String toString() {
        return "CyclesInSubgraph [cycles=" + breakingPoints + ", investigatedPackages=" + investigatedPackages + "]";
    }

    public static CyclesInSubgraph empty() {
        return new CyclesInSubgraph(Set.of(), Map.of());
    }
}
