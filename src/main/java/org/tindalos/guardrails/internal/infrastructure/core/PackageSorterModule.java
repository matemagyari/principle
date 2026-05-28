package org.tindalos.guardrails.internal.infrastructure.core;

import java.util.Comparator;
import java.util.List;

import org.tindalos.guardrails.internal.domain.core.Package;

public final class PackageSorterModule {

    private PackageSorterModule() {}

    public static List<Package> sortByName(List<Package> packages, String basePackageName) {
        return sortByName(packages).stream()
                .filter(p -> p.reference().startsWith(basePackageName))
                .toList();
    }

    public static List<Package> sortByName(List<Package> packages) {
        return packages.stream()
                .sorted(Comparator.comparing(p -> p.reference().name()))
                .toList();
    }
}
