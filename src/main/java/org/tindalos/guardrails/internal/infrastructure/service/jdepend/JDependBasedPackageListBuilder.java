package org.tindalos.guardrails.internal.infrastructure.service.jdepend;

import java.util.List;

import org.tindalos.guardrails.internal.app.PackageListBuilder;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.infrastructure.core.PackageSorterModule;

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
