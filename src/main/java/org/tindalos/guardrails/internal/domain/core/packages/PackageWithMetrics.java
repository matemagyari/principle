package org.tindalos.guardrails.internal.domain.core.packages;

import java.util.Set;

/**
 * Represents a Java package coupled with its computed architectural metrics and dependency relationships.
 * <p>
 * Provides information on internal and external package references, as well as accumulated metrics
 * for use in structural analysis, dependency checking, and quality guardrail enforcement.
 * </p>
 */
public interface PackageWithMetrics {

    /**
     * Retrieves the fully qualified {@link PackageReference} of this package.
     *
     * @return the package reference
     */
    PackageReference reference();

    /**
     * Retrieves the architectural metrics computed for this package.
     *
     * @return the package metrics containing coupling, abstractness, and instability details
     */
    PackageMetrics metrics();

    /**
     * Retrieves the set of internal packages directly referenced by this package.
     * All references in this set reside within the analyzed codebase tree.
     *
     * @return a set of internal package references
     */
    Set<PackageReference> ownPackageReferences();

    /**
     * Retrieves the set of external packages directly referenced by this package.
     * These references point outside the analyzed codebase subtree (e.g., JDK, third-party libraries).
     *
     * @return a set of external package references
     */
    Set<PackageReference> ownExternalPackageReferences();

    /**
     * Retrieves all direct internal package references originating from this package
     * or recursively from any of its sub-packages, excluding self-references.
     *
     * @return a set of accumulated direct package references
     */
    Set<PackageReference> accumulatedDirectPackageReferences();
}