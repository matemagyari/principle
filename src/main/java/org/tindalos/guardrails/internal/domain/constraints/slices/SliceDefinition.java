package org.tindalos.guardrails.internal.domain.constraints.slices;

import java.util.Set;

import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

/**
 * Represents a slice definition in a guardrails configuration, containing the slice's ID,
 * the packages it includes, and its allowed dependencies on other slices.
 */
public record SliceDefinition(
        SliceId id,
        Set<PackageReference> packages,
        Set<SliceId> legalDependencies) {

    public SliceDefinition {
        packages = Set.copyOf(packages);
        legalDependencies = Set.copyOf(legalDependencies);
    }

    public boolean overlapsWith(SliceDefinition that) {
        for (PackageReference aPackage : this.packages) {
            for (PackageReference otherPackage : that.packages) {
                if (aPackage.oneContainsAnother(otherPackage)) {
                    return true;
                }
            }
        }
        return false;
    }
}
