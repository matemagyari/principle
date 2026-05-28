package org.tindalos.guardrails.internal.domain.core.packages;

import java.util.Set;

public interface PackageWithMetrics {
    PackageReference reference();
    PackageMetrics metrics();
    Set<PackageReference> ownPackageReferences();
    Set<PackageReference> ownExternalPackageReferences();
    Set<PackageReference> accumulatedDirectPackageReferences();
}