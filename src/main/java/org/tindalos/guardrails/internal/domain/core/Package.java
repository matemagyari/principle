package org.tindalos.guardrails.internal.domain.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Represents an immutable Package with its metric values and sub-packages.
 */
public record Package(
    PackageReference reference,
    PackageMetrics metrics,
    Set<PackageReference> ownPackageReferences,
    Set<PackageReference> ownExternalPackageReferences,
    boolean isUnreferred,
    List<Package> subPackages
) implements PackageWithMetrics {

    public Package {
        ownPackageReferences = Set.copyOf(ownPackageReferences);
        ownExternalPackageReferences = Set.copyOf(ownExternalPackageReferences);
        subPackages = List.copyOf(subPackages);
    }

    // Constructor with String reference name and default empty collections
    public Package(String referenceName) {
        this(new PackageReference(referenceName), PackageMetrics.UNDEFINED, Set.of(), Set.of(), true, List.of());
    }

    // Constructor with PackageReference and default empty collections
    public Package(PackageReference reference) {
        this(reference, PackageMetrics.UNDEFINED, Set.of(), Set.of(), true, List.of());
    }

    // Constructor for compatibility with existing tests and builders that don't pass subPackages
    public Package(
            PackageReference reference,
            PackageMetrics metrics,
            Set<PackageReference> ownPackageReferences,
            Set<PackageReference> ownExternalPackageReferences,
            boolean isUnreferred) {
        this(reference, metrics, ownPackageReferences, ownExternalPackageReferences, isUnreferred, List.of());
    }

    @Override
    public Set<PackageReference> accumulatedDirectPackageReferences() {
        return Stream
            .concat(
                subPackages
                    .stream()
                    .flatMap(aPackage -> aPackage.accumulatedDirectPackageReferences().stream())
                    .filter(x -> !x.equals(reference)),
                ownPackageReferences().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    public Map<PackageReference, Package> toMap() {
        return Collections.unmodifiableMap(toMap(new HashMap<>()));
    }

    private Map<PackageReference, Package> toMap(Map<PackageReference, Package> accumulatingMap) {
        accumulatingMap.put(reference, this);
        subPackages.forEach(child -> child.toMap(accumulatingMap));
        return accumulatingMap;
    }

    public CyclesInSubgraph detectCycles(Map<PackageReference, Package> packageReferences) {
        return detectCyclesOnThePathFromHere(
            TraversedPackages.empty(),
            new CyclesInSubgraph(Set.of(), Map.of()),
            Collections.unmodifiableMap(packageReferences));
    }

    public Set<PackageReference> cumulatedDependencies(Map<PackageReference, Package> packageReferenceMap) {
        return cumulatedDependenciesAcc(packageReferenceMap, new HashSet<>());
    }

    private int indexInTraversedPath(List<PackageReference> traversedPackages) {
        int index = traversedPackages.indexOf(reference);
        if (index != -1) {
            return index;
        }

        Integer matchFoundIndex = null;
        for (int i = 0; i < traversedPackages.size() && matchFoundIndex == null; i++) {
            PackageReference possibleMatch = traversedPackages.get(i);
            if (possibleMatch.equals(reference)
                || (reference.isDescendantOf(possibleMatch)
                    && notAllAreDescendantsOf(
                        traversedPackages.subList(i + 1, traversedPackages.size()),
                        possibleMatch))) {
                matchFoundIndex = i;
            }
        }

        return matchFoundIndex == null ? -1 : matchFoundIndex;
    }

    private Set<PackageReference> cumulatedDependenciesAcc(
        Map<PackageReference, Package> packageReferenceMap,
        Set<PackageReference> dependencies) {

        Set<PackageReference> accumulatedPackageReferences = this.accumulatedDirectPackageReferences().stream()
            .filter(packageReference -> !dependencies.contains(packageReference))
            .collect(Collectors.toUnmodifiableSet());

        if (accumulatedPackageReferences.isEmpty()) {
            return dependencies.stream()
                .filter(packageReference -> !packageReference.equals(reference))
                .collect(Collectors.toUnmodifiableSet());
        }

        Set<PackageReference> result = new HashSet<>(accumulatedPackageReferences);
        accumulatedPackageReferences.forEach(packageReference -> {
            dependencies.add(packageReference);
            result.addAll(packageReferenceMap.get(packageReference).cumulatedDependenciesAcc(packageReferenceMap, dependencies));
            result.remove(reference);
        });

        return Set.copyOf(result);
    }

    private CyclesInSubgraph detectCyclesOnThePathFromHere(
        TraversedPackages traversedPackages,
        CyclesInSubgraph foundCycles,
        Map<PackageReference, Package> packageReferences) {

        // enough cycles have been found already with this package
        if (foundCycles.isBreakingPoint(this)) {
            return foundCycles;
        }

        var cyclesAfterInvestigating = foundCycles.withInvestigatedPackage(this);

        Optional<List<PackageReference>> cycleCandidateEndingHere = findCycleCandidateEndingHere(traversedPackages);
        return cycleCandidateEndingHere
                .map(candidate -> isValid(candidate)
                        ? cyclesAfterInvestigating.withAddedCycle(new Cycle(candidate))
                        : cyclesAfterInvestigating)
                .orElseGet(() -> {
                    // Process all referred packages sequentially, threading the accumulator through
                    var accumulatedCycles = cyclesAfterInvestigating;
                    for (var referencedPackage : accumulatedDirectlyReferredPackages(packageReferences)) {
                        CyclesInSubgraph cyclesInSubgraph = referencedPackage.detectCyclesOnThePathFromHere(
                                traversedPackages.add(reference),
                                accumulatedCycles,
                                packageReferences);
                        accumulatedCycles = accumulatedCycles.mergedWith(cyclesInSubgraph);
                    }
                    return accumulatedCycles;
                });
    }

    private Optional<List<PackageReference>> findCycleCandidateEndingHere(TraversedPackages traversedPackages) {
        int indexOfThisPackage = indexInTraversedPath(traversedPackages.packages());
        if (indexOfThisPackage > -1) {
            return Optional.of(traversedPackages.from(indexOfThisPackage));
        }
        return Optional.empty();
    }

    private boolean notAllAreDescendantsOf(List<PackageReference> packages, PackageReference possibleAncestor) {
        return packages.stream().anyMatch(pkg -> !pkg.isDescendantOf(possibleAncestor));
    }

    private Set<Package> accumulatedDirectlyReferredPackages(Map<PackageReference, Package> packageReferenceMap) {
        return accumulatedDirectPackageReferences().stream()
            .flatMap(r -> Optional.ofNullable(packageReferenceMap.get(r)).stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    private boolean notEveryNodeUnderFirst(List<PackageReference> cycleCandidate) {
        PackageReference first = cycleCandidate.getFirst();
        boolean hasNonDescendant = cycleCandidate.stream().skip(1).anyMatch(p -> !p.isDescendantOf(first));

        if (!hasNonDescendant) {
            return first.equals(reference);
        }

        return true;
    }

    private boolean isValid(List<PackageReference> cycleCandidate) {
        if (cycleCandidate.size() < 2) {
            return false;
        }
        return notEveryNodeUnderFirst(cycleCandidate);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Package castOther && castOther.reference.equals(reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }

    @Override
    public String toString() {
        return reference.toString();
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
            List<PackageReference> next = new ArrayList<>(packages);
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
