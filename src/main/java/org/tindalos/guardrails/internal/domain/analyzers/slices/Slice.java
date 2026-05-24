package org.tindalos.guardrails.internal.domain.analyzers.slices;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.guardrails.internal.domain.constraints.slices.InvalidSliceDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Concrete representation of a slice in a slice group, combining the definition
 * with actual project packages and calculating illegal or missing dependencies.
 */
public class Slice {

    public final SliceId id;
    public final Set<PackageWithMetrics> packagesUnderSlice;
    public final Set<SliceId> plannedDependencies;
    private final Set<PackageReference> outgoingReferences;

    public Slice(SliceId id, Set<PackageWithMetrics> packagesUnderSlice, Set<SliceId> plannedDependencies) {
        if (plannedDependencies.contains(id)) {
            throw new InvalidSliceDefinitionException("Slice should not depend on itself: " + id.value());
        }
        this.id = id;
        this.packagesUnderSlice = Set.copyOf(packagesUnderSlice);
        this.plannedDependencies = Set.copyOf(plannedDependencies);
        this.outgoingReferences = this.packagesUnderSlice.stream()
                .flatMap(p -> p.accumulatedDirectPackageReferences().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Slice> findMissingPredefinedDependencies(Set<Slice> otherSlices) {
        assert !otherSlices.contains(this);
        return otherSlices.stream()
                .filter(x -> plannedDependencies.contains(x.id))
                .filter(x -> !x.isReferredBy(outgoingReferences))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Slice> findIllegalDependencies(Set<Slice> otherSlices) {
        assert !otherSlices.contains(this);
        boolean hasIllegalDependencies = otherSlices.stream()
                .anyMatch(x -> !plannedDependencies.contains(x.id));
        if (!hasIllegalDependencies) return Set.of();
        Set<Slice> legalDependencies = otherSlices.stream()
                .filter(x -> plannedDependencies.contains(x.id))
                .collect(Collectors.toSet());
        Set<PackageReference> extraReferences = calculateExtraReferences(legalDependencies);
        return otherSlices.stream()
                .filter(x -> x.isReferredBy(extraReferences))
                .collect(Collectors.toUnmodifiableSet());
    }

    private boolean isReferredBy(Set<PackageReference> references) {
        return references.stream().anyMatch(ref ->
                packagesUnderSlice.stream().anyMatch(pkg ->
                        ref.pointsToThatOrInside(pkg.reference())));
    }

    private Set<PackageReference> calculateExtraReferences(Set<Slice> legalDependencies) {
        Set<PackageReference> potentiallyIllegal = new java.util.HashSet<>(outgoingReferences);
        for (Slice legalDependency : legalDependencies) {
            potentiallyIllegal = legalDependency.removeOutsideReferences(potentiallyIllegal);
        }
        return potentiallyIllegal;
    }

    private Set<PackageReference> removeOutsideReferences(Set<PackageReference> references) {
        return references.stream()
                .filter(ref -> packagesUnderSlice.stream()
                        .noneMatch(pkg -> ref.pointsToThatOrInside(pkg.reference())))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Slice)) return false;
        return ((Slice) other).id.equals(id);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

    @Override
    public String toString() {
        return id.value();
    }
}
