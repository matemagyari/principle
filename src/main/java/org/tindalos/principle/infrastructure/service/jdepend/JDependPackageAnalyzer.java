package org.tindalos.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;

public class JDependPackageAnalyzer implements PackageAnalyzer {
    
    private final JDependRunner jDependRunner;
    private final PackageListFactory packageListFactory;

    public JDependPackageAnalyzer(JDependRunner jDependRunner, PackageListFactory packageListFactory) {
        this.jDependRunner = jDependRunner;
        this.packageListFactory = packageListFactory;
    }

    public List<Package> analyze(DesignQualityCheckConfiguration parameters) {
        Collection<JavaPackage> analyzedPackages = jDependRunner.getAnalyzedPackages(parameters.getBasePackage());
        return packageListFactory.build(analyzedPackages);
    }

}
