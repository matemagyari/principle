package org.tindalos.principle.domain.core.packages;

import java.util.Set;

public interface PackageWithMetrics {
    PackageReference reference();
    PackageMetrics getMetrics();
    Set<PackageReference> accumulatedDirectPackageReferences();
}