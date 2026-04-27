package org.tindalos.principle.internal.infrastructure.di;

import java.util.List;

import org.tindalos.principle.internal.domain.core.Package;
import org.tindalos.principle.internal.infrastructure.core.PackageSorterModule;
import org.tindalos.principle.internal.domain.core.PackageStructureBuilder;

public final class PackageStructureBuilderImpl implements PackageStructureBuilder {

    private Package cachedBasePackage;

    @Override
    public Package build(List<Package> packages, String rootPackage) {
        if (cachedBasePackage == null) {
            List<Package> sortedPackages = PackageSorterModule.sortByName(packages, rootPackage);
            Package basePackage = sortedPackages.getFirst();
            sortedPackages.stream().skip(1).forEach(basePackage::insert);
            cachedBasePackage = basePackage;
        }
        return cachedBasePackage;
    }
}
