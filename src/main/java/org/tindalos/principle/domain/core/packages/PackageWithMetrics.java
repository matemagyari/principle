package org.tindalos.principle.domain.core.packages;

import java.util.Set;

public interface PackageWithMetrics {
    PackageReference reference();
    PackageMetrics getMetrics();
    Set<PackageReference> getOwnPackageReferences();
    Set<PackageReference> getOwnExternalPackageReferences();
    Set<PackageReference> accumulatedDirectPackageReferences();
}