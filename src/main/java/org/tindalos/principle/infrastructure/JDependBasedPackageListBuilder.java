package org.tindalos.principle.infrastructure;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageSorterModule;
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner;
import org.tindalos.principle.infrastructure.service.jdepend.PackageFactory;

import jdepend.framework.JavaPackage;

public final class JDependBasedPackageListBuilder implements PackageListBuilder {

    private final String rootPackage;
    private final PackageFactory packageFactory;

    public JDependBasedPackageListBuilder(String rootPackage) {
        this.rootPackage = rootPackage;
        this.packageFactory = new PackageFactory(rootPackage);
    }

    @Override
    public List<Package> build() {
        List<JavaPackage> analyzedPackages = JDependRunner.preparePackages(rootPackage, true);
        return packageFactory.buildPackageListFactory(PackageSorterModule::sortByName).apply(analyzedPackages);
    }
}
