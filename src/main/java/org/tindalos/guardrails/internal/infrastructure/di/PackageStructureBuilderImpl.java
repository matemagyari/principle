package org.tindalos.guardrails.internal.infrastructure.di;

import java.util.List;

import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.infrastructure.core.PackageSorterModule;
import org.tindalos.guardrails.internal.infrastructure.packages.MutablePackage;

public final class PackageStructureBuilderImpl implements PackageStructureBuilder {

    private Package cachedBasePackage;

    @Override
    public Package build(List<Package> packages, String rootPackage) {
        if (cachedBasePackage == null) {
            List<Package> sortedPackages = PackageSorterModule.sortByName(packages, rootPackage);
            
            List<MutablePackage> mutablePackages = sortedPackages.stream()
                .map(pkg -> new MutablePackage(
                    pkg.reference(),
                    pkg.metrics(),
                    pkg.ownPackageReferences(),
                    pkg.ownExternalPackageReferences(),
                    pkg.isUnreferred()
                ))
                .toList();

            MutablePackage baseMutablePackage = mutablePackages.getFirst();
            mutablePackages.stream().skip(1).forEach(baseMutablePackage::insert);
            cachedBasePackage = baseMutablePackage.toImmutable();
        }
        return cachedBasePackage;
    }
}
