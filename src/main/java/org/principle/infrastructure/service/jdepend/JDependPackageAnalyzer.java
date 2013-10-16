package org.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.principle.app.service.PackageAnalyzer;
import org.principle.domain.core.DesingCheckerParameters;

public class JDependPackageAnalyzer implements PackageAnalyzer {
    
    private final JDependRunner jDependRunner;
    private final PackageBuilder packageBuilder;

    public JDependPackageAnalyzer(JDependRunner jDependRunner, PackageBuilder packageBuilder) {
        this.jDependRunner = jDependRunner;
        this.packageBuilder = packageBuilder;
    }

    public List<org.principle.domain.detector.cycledetector.core.Package> analyze(DesingCheckerParameters parameters) {
        Collection<JavaPackage> analyzedPackages = jDependRunner.getAnalyzedPackages(parameters.getBasePackage());
        return packageBuilder.build(analyzedPackages, parameters.getBasePackage());
    }

}
