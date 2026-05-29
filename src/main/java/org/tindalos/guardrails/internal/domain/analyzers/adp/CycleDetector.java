package org.tindalos.guardrails.internal.domain.analyzers.adp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;

/**
 * Analyzer for the Acyclic Dependency Principle (ADP).
 * Builds the package tree for the configured base package and detects cycles.
 */
public final class CycleDetector implements Analyzer {

    private final PackageStructureBuilder packageStructureBuilder;

    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = Objects.requireNonNull(packageStructureBuilder, "packageStructureBuilder");
    }

    @Override
    public ADPResult analyze(AnalysisInput input) {
        var basePackage = packageStructureBuilder.build(input.packages(), input.analysisPlan().basePackage());
        var references = basePackage.toMap();

        var sortedByAfferents = references.values().stream()
            .filter(pkg -> basePackage.metrics().afferentCoupling() > 0 || !pkg.equals(basePackage))
            .sorted(Comparator.<Package>comparingInt(pkg -> pkg.metrics().afferentCoupling())
                .thenComparing(Package::reference))
            .toList();

        var cycles = analyzeCycles(sortedByAfferents, references);

        var expectation = input.packageCouplingExpectations()
            .flatMap(pc -> pc.adp())
            .orElseThrow();
            
        return new ADPResult(cycles.cycles(), expectation);
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling().flatMap(pc -> pc.adp()).isPresent();
    }

    /**
     * Iteratively analyzes cycles across all remaining packages, tracking and skipping 
     * already investigated package components to prevent redundant work.
     */
    private CyclesInSubgraph analyzeCycles(
            List<Package> packagesToInvestigate,
            Map<PackageReference, Package> packageReferences) {

        var accumulator = CyclesInSubgraph.empty();
        var remaining = new java.util.ArrayList<>(packagesToInvestigate);

        while (!remaining.isEmpty()) {
            var current = remaining.removeFirst();
            var cyclesInSubgraph = detectCycles(current, packageReferences);
            accumulator = accumulator.mergedWith(cyclesInSubgraph);

            var investigated = cyclesInSubgraph.investigatedPackages();
            remaining.removeIf(investigated::contains);
        }
        return accumulator;
    }

    private CyclesInSubgraph detectCycles(Package startPackage, Map<PackageReference, Package> packageReferences) {
        return detectCyclesOnThePathFromHere(
            startPackage,
            TraversedPackages.empty(),
            CyclesInSubgraph.empty(),
            packageReferences);
    }

    private CyclesInSubgraph detectCyclesOnThePathFromHere(
            Package currentPackage,
            TraversedPackages traversedPackages,
            CyclesInSubgraph foundCycles,
            Map<PackageReference, Package> packageReferences) {

        // backtrack if enough cycles have been found already with this package
        if (foundCycles.isBreakingPoint(currentPackage)) {
            return foundCycles;
        }

        var cyclesAfterInvestigating = foundCycles.withInvestigatedPackage(currentPackage);

        var cycleCandidate = findCycleCandidateEndingHere(currentPackage, traversedPackages);
        if (cycleCandidate.isPresent()) {
            var candidate = cycleCandidate.get();
            if (isValid(currentPackage, candidate)) {
                return cyclesAfterInvestigating.withAddedCycle(new Cycle(candidate));
            }
            return cyclesAfterInvestigating;
        }

        return traverseReferredPackages(currentPackage, traversedPackages, cyclesAfterInvestigating, packageReferences);
    }

    private CyclesInSubgraph traverseReferredPackages(
            Package currentPackage,
            TraversedPackages traversedPackages,
            CyclesInSubgraph accumulatedCycles,
            Map<PackageReference, Package> packageReferences) {

        var nextTraversed = traversedPackages.add(currentPackage.reference());
        var currentAccumulator = accumulatedCycles;

        for (var referencedPackage : accumulatedDirectlyReferredPackages(currentPackage, packageReferences)) {
            var subgraphCycles = detectCyclesOnThePathFromHere(
                    referencedPackage,
                    nextTraversed,
                    currentAccumulator,
                    packageReferences);
            currentAccumulator = currentAccumulator.mergedWith(subgraphCycles);
        }
        return currentAccumulator;
    }

    private Optional<List<PackageReference>> findCycleCandidateEndingHere(Package currentPackage, TraversedPackages traversedPackages) {
        int indexOfThisPackage = indexInTraversedPath(currentPackage, traversedPackages.packages());
        if (indexOfThisPackage > -1) {
            return Optional.of(traversedPackages.from(indexOfThisPackage));
        }
        return Optional.empty();
    }

    private int indexInTraversedPath(Package currentPackage, List<PackageReference> traversedPackages) {
        var currentRef = currentPackage.reference();
        int directIndex = traversedPackages.indexOf(currentRef);
        if (directIndex != -1) {
            return directIndex;
        }

        for (int i = 0; i < traversedPackages.size(); i++) {
            var possibleMatch = traversedPackages.get(i);
            if (currentRef.isDescendantOf(possibleMatch)) {
                var subsequentPackages = traversedPackages.subList(i + 1, traversedPackages.size());
                boolean hasNonDescendant = subsequentPackages.stream()
                        .anyMatch(pkg -> !pkg.isDescendantOf(possibleMatch));
                if (hasNonDescendant) {
                    return i;
                }
            }
        }
        return -1;
    }

    private List<Package> accumulatedDirectlyReferredPackages(Package currentPackage, Map<PackageReference, Package> packageReferenceMap) {
        return currentPackage.accumulatedDirectPackageReferences().stream()
            .map(packageReferenceMap::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.<Package>comparingInt(pkg -> pkg.metrics().afferentCoupling())
                .thenComparing(Package::reference))
            .toList();
    }

    private boolean notEveryNodeUnderFirst(Package currentPackage, List<PackageReference> cycleCandidate) {
        var first = cycleCandidate.getFirst();
        boolean hasNonDescendant = cycleCandidate.stream()
                .skip(1)
                .anyMatch(p -> !p.isDescendantOf(first));

        if (hasNonDescendant) {
            return true;
        }
        return first.equals(currentPackage.reference());
    }

    private boolean isValid(Package currentPackage, List<PackageReference> cycleCandidate) {
        if (cycleCandidate.size() < 2) {
            return false;
        }
        return notEveryNodeUnderFirst(currentPackage, cycleCandidate);
    }

    /**
     * Tracks the stack of currently traversed package references in the active DFS path.
     */
    private record TraversedPackages(List<PackageReference> packages) {

        static TraversedPackages empty() {
            return new TraversedPackages(List.of());
        }

        TraversedPackages {
            packages = List.copyOf(packages);
        }

        TraversedPackages add(PackageReference reference) {
            var next = new java.util.ArrayList<>(packages);
            next.add(reference);
            return new TraversedPackages(next);
        }

        List<PackageReference> from(int index) {
            return packages.subList(index, packages.size());
        }
    }
}