package org.tindalos.principle.infrastructure.service.jdepend;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;

public class PackageFactory {
    
    private final MetricsCalculator metricsCalculator;
	private String basePackage;
    
    public PackageFactory(MetricsCalculator metricsCalculator, String basePackage) {
        this.metricsCalculator = metricsCalculator;
		this.basePackage = basePackage;
    }

    Package transform(JavaPackage javaPackage) {
        
		Metrics metrics = metricsCalculator.calculate(javaPackage);
        return new LazyLoadingJDependBasedPackage(javaPackage, metrics, this);
    }

	public boolean isRelevant(JavaPackage javaPackage) {
		return javaPackage.getName().startsWith(basePackage);
	}
}
