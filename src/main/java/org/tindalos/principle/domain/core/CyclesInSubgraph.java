package org.tindalos.principle.domain.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.core.packages.PackageReference;

/**
 * Tracks cycle detection progress in a package subgraph.
 * Stores cycles grouped by their breaking points and remembers already investigated packages.
 */
public class CyclesInSubgraph {

    public static final int LIMIT = 5;

    private final Set<Package> investigatedPackages = new HashSet<>();
    private final Map<PackageReference, Set<Cycle>> breakingPoints = new HashMap<>();

    public Set<Package> investigatedPackages() {
        return Set.copyOf(investigatedPackages);
    }

    public Map<PackageReference, Set<Cycle>> cycles() {
        return breakingPoints.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> Set.copyOf(entry.getValue())));
    }

    public void add(Cycle cycle) {
        boolean exists = breakingPoints.values().stream().anyMatch(cycles -> cycles.contains(cycle));
        if (!exists) {
            breakingPoints.computeIfAbsent(cycle.end(), key -> new HashSet<>()).add(cycle);
        }
    }

    public void rememberPackageAsInvestigated(Package aPackage) {
        investigatedPackages.add(aPackage);
    }

    public void mergeIn(CyclesInSubgraph that) {
        that.breakingPoints.values().forEach(cycles -> cycles.forEach(this::add));
        investigatedPackages.addAll(that.investigatedPackages());
    }

    public Map<PackageReference, Set<Cycle>> mergeBreakingPoints2(Map<PackageReference, Set<Cycle>> breakingPointsInOther) {
        breakingPointsInOther.values().forEach(cycles -> cycles.forEach(this::add));
        return cycles();
    }

    public boolean isBreakingPoint(Package aPackage) {
        Set<Cycle> cyclesForThisBreakingPoint = breakingPoints.get(aPackage.reference());
        return cyclesForThisBreakingPoint != null && cyclesForThisBreakingPoint.size() > LIMIT;
    }

    @Override
    public String toString() {
        return "CyclesInSubgraph [cycles=" + breakingPoints + ", investigatedPackages=" + investigatedPackages() + "]";
    }

    public static CyclesInSubgraph empty() {
        return new CyclesInSubgraph();
    }
}
