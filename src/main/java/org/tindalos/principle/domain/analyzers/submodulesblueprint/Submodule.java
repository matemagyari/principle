package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

/**
 * Represents a submodule in the blueprint definition, grouping packages into a named module
 * and declaring which other submodules it is allowed to depend on.
 */
public class Submodule {

    public final SubmoduleId id;
    public final Set<PackageWithMetrics> packagesUnderModule;
    public final Set<SubmoduleId> plannedDependencies;
    private final Set<PackageReference> outgoingReferences;

    public Submodule(SubmoduleId id, Set<PackageWithMetrics> packagesUnderModule, Set<SubmoduleId> plannedDependencies) {
        if (plannedDependencies.contains(id))
            throw new InvalidBlueprintDefinitionException("Submodule should not depend on itself: " + id);
        this.id = id;
        this.packagesUnderModule = Set.copyOf(packagesUnderModule);
        this.plannedDependencies = Set.copyOf(plannedDependencies);
        this.outgoingReferences = this.packagesUnderModule.stream()
                .flatMap(p -> p.accumulatedDirectPackageReferences().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Submodule> findMissingPredefinedDependencies(Set<Submodule> otherSubmodules) {
        assert !otherSubmodules.contains(this);
        return otherSubmodules.stream()
                .filter(x -> plannedDependencies.contains(x.id))
                .filter(x -> !x.isReferredBy(outgoingReferences))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Submodule> findIllegalDependencies(Set<Submodule> otherSubmodules) {
        assert !otherSubmodules.contains(this);
        boolean hasIllegalDependencies = otherSubmodules.stream()
                .anyMatch(x -> !plannedDependencies.contains(x.id));
        if (!hasIllegalDependencies) return Set.of();
        Set<Submodule> legalDependencies = otherSubmodules.stream()
                .filter(x -> plannedDependencies.contains(x.id))
                .collect(Collectors.toSet());
        Set<PackageReference> extraReferences = calculateExtraReferences(legalDependencies);
        return otherSubmodules.stream()
                .filter(x -> x.isReferredBy(extraReferences))
                .collect(Collectors.toUnmodifiableSet());
    }

    private boolean isReferredBy(Set<PackageReference> references) {
        return references.stream().anyMatch(ref ->
                packagesUnderModule.stream().anyMatch(pkg ->
                        ref.pointsToThatOrInside(pkg.reference())));
    }

    private Set<PackageReference> calculateExtraReferences(Set<Submodule> legalDependencies) {
        Set<PackageReference> potentiallyIllegal = new java.util.HashSet<>(outgoingReferences);
        for (Submodule legalDependency : legalDependencies) {
            potentiallyIllegal = legalDependency.removeOutsideReferences(potentiallyIllegal);
        }
        return potentiallyIllegal;
    }

    private Set<PackageReference> removeOutsideReferences(Set<PackageReference> references) {
        return references.stream()
                .filter(ref -> packagesUnderModule.stream()
                        .noneMatch(pkg -> ref.pointsToThatOrInside(pkg.reference())))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Submodule)) return false;
        return ((Submodule) other).id.equals(id);
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

