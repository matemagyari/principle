package org.tindalos.principle.infrastructure.di;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.infrastructure.core.PackageSorterModule;
import org.tindalos.principle.domain.core.PackageStructureBuilder;

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
