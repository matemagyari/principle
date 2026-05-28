package org.tindalos.guardrails.internal.infrastructure.service.jdepend;

import java.util.Set;

import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Immutable package representation containing metrics and references.
 * Decoupled from JDepend framework.
 *
 * @param reference the package reference
 * @param metrics the package metrics
 * @param ownPackageReferences dependencies inside the analyzed package tree
 * @param ownExternalPackageReferences dependencies outside the analyzed package tree
 * @param isUnreferred true if there are no afferent (incoming) couplings
 */
public record LazyLoadingJDependBasedPackage(
    PackageReference reference,
    PackageMetrics metrics,
    Set<PackageReference> ownPackageReferences,
    Set<PackageReference> ownExternalPackageReferences,
    boolean isUnreferred
) implements PackageWithMetrics {

    public LazyLoadingJDependBasedPackage {
        ownPackageReferences = Set.copyOf(ownPackageReferences);
        ownExternalPackageReferences = Set.copyOf(ownExternalPackageReferences);
    }

    @Override
    public Set<PackageReference> accumulatedDirectPackageReferences() {
        return ownPackageReferences;
    }
}