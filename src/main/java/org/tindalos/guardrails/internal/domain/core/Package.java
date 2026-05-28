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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

public class Package implements PackageWithMetrics {

    private final PackageReference reference;
    private final List<Package> subPackages = new ArrayList<>();

    private final PackageMetrics metrics;
    private final Set<PackageReference> ownPackageReferences;
    private final Set<PackageReference> ownExternalPackageReferences;
    private final boolean unreferred;

    protected Package(PackageReference reference) {
        this.reference = reference;
        this.metrics = PackageMetrics.UNDEFINED;
        this.ownPackageReferences = Collections.emptySet();
        this.ownExternalPackageReferences = Collections.emptySet();
        this.unreferred = true;
    }

    protected Package(String referenceName) {
        this(new PackageReference(referenceName));
    }

    public Package(
            PackageReference reference,
            PackageMetrics metrics,
            Set<PackageReference> ownPackageReferences,
            Set<PackageReference> ownExternalPackageReferences,
            boolean unreferred) {
        this.reference = reference;
        this.metrics = metrics;
        this.ownPackageReferences = Set.copyOf(ownPackageReferences);
        this.ownExternalPackageReferences = Set.copyOf(ownExternalPackageReferences);
        this.unreferred = unreferred;
    }

    @Override
    public PackageReference reference() {
        return reference;
    }

    public List<Package> subPackages() {
        return Collections.unmodifiableList(subPackages);
    }

    @Override
    public PackageMetrics metrics() {
        return metrics;
    }

    @Override
    public Set<PackageReference> ownPackageReferences() {
        return ownPackageReferences;
    }

    @Override
    public Set<PackageReference> ownExternalPackageReferences() {
        return ownExternalPackageReferences;
    }

    // all the references going out from this package
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

    public boolean isUnreferred() {
        return unreferred;
    }

    public Map<PackageReference, Package> toMap() {
        return Collections.unmodifiableMap(toMap(new HashMap<>()));
    }

    public CyclesInSubgraph detectCycles(Map<PackageReference, Package> packageReferences) {
        return detectCyclesOnThePathFromHere(
            TraversedPackages.empty(),
            new CyclesInSubgraph(Set.of(), Map.of()),
            Collections.unmodifiableMap(packageReferences));
    }

    // it dies if there are cycles
    // through references, not through subPackages. transaitive too
    public Set<PackageReference> cumulatedDependencies(Map<PackageReference, Package> packageReferenceMap) {
        return cumulatedDependenciesAcc(packageReferenceMap, new HashSet<>());
    }

    public void insert(Package aPackage) {
        if (this.equals(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert into itself " + this);
        } else if (doesNotContain(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this);
        } else if (isDirectSuperPackageOf(aPackage)) {
            subPackages.add(aPackage);
        } else {
            insertIndirectSubPackage(aPackage);
        }
    }

    private Set<Package> accumulatedDirectlyReferredPackages(Map<PackageReference, Package> packageReferenceMap) {
        return accumulatedDirectPackageReferences().stream()
            .flatMap(r -> Optional.ofNullable(packageReferenceMap.get(r)).stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    private Map<PackageReference, Package> toMap(Map<PackageReference, Package> accumulatingMap) {
        accumulatingMap.put(reference, this);
        subPackages.forEach(child -> child.toMap(accumulatingMap));
        return accumulatingMap;
    }

    private Package getSubPackageByRelativeName(String relativeName) {
        PackageReference targetReference = reference.child(relativeName);

        return subPackages.stream()
            .filter(subPackage -> subPackage.reference.equals(targetReference))
            .findFirst()
            .orElseGet(() -> {
                Package directSubPackage = new Package(reference.createChild(relativeName)) {
                    @Override
                    public Set<PackageReference> ownPackageReferences() {
                        return Collections.emptySet();
                    }

                    @Override
                    public Set<PackageReference> ownExternalPackageReferences() {
                        return Collections.emptySet();
                    }

                    @Override
                    public PackageMetrics metrics() {
                        return PackageMetrics.UNDEFINED;
                    }

                    @Override
                    public boolean isUnreferred() {
                        return true;
                    }
                };

                subPackages.add(directSubPackage);
                return directSubPackage;
            });
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

    private boolean isDirectSuperPackageOf(Package aPackage) {
        return reference.isDirectParentOf(aPackage.reference);
    }

    private boolean doesNotContain(Package aPackage) {
        return !aPackage.reference.pointsInside(reference);
    }

    private String firstPartOfRelativeNameTo(Package parentPackage) {
        return reference.firstPartOfRelativeNameTo(parentPackage.reference);
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

    private void insertIndirectSubPackage(Package aPackage) {
        String relativeNameOfDirectSubPackage = aPackage.firstPartOfRelativeNameTo(this);
        getSubPackageByRelativeName(relativeNameOfDirectSubPackage).insert(aPackage);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Package castOther && castOther.reference.equals(reference);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(reference).hashCode();
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
