package org.tindalos.guardrails.internal.domain.analyzers.adp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
            .sorted(Comparator.comparingInt(pkg -> pkg.metrics().afferentCoupling()))
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        if (basePackage.metrics().afferentCoupling() == 0) {
            sortedByAfferents.removeIf(basePackage::equals);
        }
        
        var cycles = analyzeCyclesRecursively(
            sortedByAfferents, 
            references, 
            new CyclesInSubgraph(Set.of(), Map.of()));

        var expectation = input.packageCouplingExpectations().flatMap(packageCoupling -> packageCoupling.adp()).orElseThrow();
        return new ADPResult(cycles.cycles(), expectation);
    }

    /**
     * Recursively analyzes cycles by processing packages in sorted order,
     * removing already-investigated packages to avoid redundant analysis.
     */
    private CyclesInSubgraph analyzeCyclesRecursively(
        java.util.List<Package> remaining,
        Map<PackageReference, Package> references,
        CyclesInSubgraph accumulator) {
        
        if (remaining.isEmpty()) {
            return accumulator;
        }
        
        var current = remaining.get(0);
        var cyclesInSubgraph = detectCycles(current, references);
        var updatedAccumulator = accumulator.mergedWith(cyclesInSubgraph);
        
        var investigatedPackages = cyclesInSubgraph.investigatedPackages();
        var next = remaining.stream()
            .skip(1)
            .filter(pkg -> !investigatedPackages.contains(pkg))
            .toList();
        
        return analyzeCyclesRecursively(new java.util.ArrayList<>(next), references, updatedAccumulator);
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling().flatMap(pc -> pc.adp()).isPresent();
    }

    private CyclesInSubgraph detectCycles(Package startPackage, Map<PackageReference, Package> packageReferences) {
        return detectCyclesOnThePathFromHere(
            startPackage,
            TraversedPackages.empty(),
            new CyclesInSubgraph(Set.of(), Map.of()),
            packageReferences);
    }

    private CyclesInSubgraph detectCyclesOnThePathFromHere(
        Package currentPackage,
        TraversedPackages traversedPackages,
        CyclesInSubgraph foundCycles,
        Map<PackageReference, Package> packageReferences) {

        // enough cycles have been found already with this package
        if (foundCycles.isBreakingPoint(currentPackage)) {
            return foundCycles;
        }

        var cyclesAfterInvestigating = foundCycles.withInvestigatedPackage(currentPackage);

        Optional<List<PackageReference>> cycleCandidateEndingHere = findCycleCandidateEndingHere(currentPackage, traversedPackages);
        return cycleCandidateEndingHere
                .map(candidate -> isValid(currentPackage, candidate)
                        ? cyclesAfterInvestigating.withAddedCycle(new Cycle(candidate))
                        : cyclesAfterInvestigating)
                .orElseGet(() -> {
                    // Process all referred packages sequentially, threading the accumulator through
                    var accumulatedCycles = cyclesAfterInvestigating;
                    for (var referencedPackage : accumulatedDirectlyReferredPackages(currentPackage, packageReferences)) {
                        CyclesInSubgraph cyclesInSubgraph = detectCyclesOnThePathFromHere(
                                referencedPackage,
                                traversedPackages.add(currentPackage.reference()),
                                accumulatedCycles,
                                packageReferences);
                        accumulatedCycles = accumulatedCycles.mergedWith(cyclesInSubgraph);
                    }
                    return accumulatedCycles;
                });
    }

    private Optional<List<PackageReference>> findCycleCandidateEndingHere(Package currentPackage, TraversedPackages traversedPackages) {
        int indexOfThisPackage = indexInTraversedPath(currentPackage, traversedPackages.packages());
        if (indexOfThisPackage > -1) {
            return Optional.of(traversedPackages.from(indexOfThisPackage));
        }
        return Optional.empty();
    }

    private int indexInTraversedPath(Package currentPackage, List<PackageReference> traversedPackages) {
        int index = traversedPackages.indexOf(currentPackage.reference());
        if (index != -1) {
            return index;
        }

        Integer matchFoundIndex = null;
        for (int i = 0; i < traversedPackages.size() && matchFoundIndex == null; i++) {
            PackageReference possibleMatch = traversedPackages.get(i);
            if (possibleMatch.equals(currentPackage.reference())
                || (currentPackage.reference().isDescendantOf(possibleMatch)
                    && notAllAreDescendantsOf(
                        traversedPackages.subList(i + 1, traversedPackages.size()),
                        possibleMatch))) {
                matchFoundIndex = i;
            }
        }

        return matchFoundIndex == null ? -1 : matchFoundIndex;
    }

    private boolean notAllAreDescendantsOf(List<PackageReference> packages, PackageReference possibleAncestor) {
        return packages.stream().anyMatch(pkg -> !pkg.isDescendantOf(possibleAncestor));
    }

    private Set<Package> accumulatedDirectlyReferredPackages(Package currentPackage, Map<PackageReference, Package> packageReferenceMap) {
        return currentPackage.accumulatedDirectPackageReferences().stream()
            .flatMap(r -> Optional.ofNullable(packageReferenceMap.get(r)).stream())
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private boolean notEveryNodeUnderFirst(Package currentPackage, List<PackageReference> cycleCandidate) {
        PackageReference first = cycleCandidate.getFirst();
        boolean hasNonDescendant = cycleCandidate.stream().skip(1).anyMatch(p -> !p.isDescendantOf(first));

        if (!hasNonDescendant) {
            return first.equals(currentPackage.reference());
        }

        return true;
    }

    private boolean isValid(Package currentPackage, List<PackageReference> cycleCandidate) {
        if (cycleCandidate.size() < 2) {
            return false;
        }
        return notEveryNodeUnderFirst(currentPackage, cycleCandidate);
    }

    private static final class TraversedPackages {
        private final List<PackageReference> packages;

        private TraversedPackages() {
            this(List.of());
        }

        private TraversedPackages(List<PackageReference> packages) {
            this.packages = packages;
        }

        private List<PackageReference> packages() {
            return packages;
        }

        private TraversedPackages add(PackageReference reference) {
            List<PackageReference> next = new java.util.ArrayList<>(packages);
            next.add(reference);
            return new TraversedPackages(List.copyOf(next));
        }

        private List<PackageReference> from(int index) {
            return packages.subList(index, packages.size());
        }

        private static TraversedPackages empty() {
            return new TraversedPackages();
        }
    }
}