package org.tindalos.principle.infrastructure.service.jdepend;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;

public class PackageTransformer {
    
    private final MetricsCalculator metricsCalculator;
    
    public PackageTransformer(MetricsCalculator metricsCalculator) {
        this.metricsCalculator = metricsCalculator;
    }

    Package transform(String basePackage,
            JavaPackage javaPackage) {
        
        Metrics metrics = metricsCalculator.calculate(javaPackage);
        return new LazyLoadingJDependBasedPackage(javaPackage, basePackage, metrics, this);
    }
}
