package org.tindalos.principle.domain.core.packages;

public interface PackageWithMetrics {
    PackageReference reference();
    PackageMetrics getMetrics();
}