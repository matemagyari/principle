package org.tindalos.guardrails.internal.domain.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public Set<PackageReference> cumulatedDependencies(Map<PackageReference, Package> packageReferenceMap) {
        return cumulatedDependenciesAcc(packageReferenceMap, new HashSet<>());
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
}
