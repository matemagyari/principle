package org.tindalos.principle.internal.infrastructure.core;

import java.util.Comparator;
import java.util.List;

import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;

public final class PackageSorterModule {

    private PackageSorterModule() {}

    public static <T extends PackageWithMetrics> List<T> sortByName(List<T> packages, String basePackageName) {
        return sortByName(packages).stream()
                .filter(p -> p.reference().startsWith(basePackageName))
                .toList();
    }

    public static <T extends PackageWithMetrics> List<T> sortByName(List<T> packages) {
        return packages.stream()
                .sorted(Comparator.comparing(p -> p.reference().name()))
                .toList();
    }
}
