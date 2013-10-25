package org.tindalos.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.Set;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Sets;

public class LazyLoadingJDependBasedPackage extends Package {
	
    private final JavaPackage javaPackage;
    private final String basePackage;
    private final PackageTransformer packageTransformer;
    private final Metrics metrics;

	public LazyLoadingJDependBasedPackage(JavaPackage javaPackage, String basePackage, Metrics metrics, PackageTransformer packageTransformer) {
        super(javaPackage.getName());
		this.javaPackage = javaPackage;
        this.basePackage = basePackage;
        this.metrics = metrics;
        this.packageTransformer = packageTransformer;
    }

    @SuppressWarnings("unchecked")
    public Set<PackageReference> getOwnPackageReferences() {

        Set<PackageReference> packages = Sets.newHashSet();

        Collection<JavaPackage> efferents = javaPackage.getEfferents();
        for (JavaPackage javaPackage : efferents) {
            if (javaPackage.getName().startsWith(basePackage)) {
                packages.add(packageTransformer.transform(basePackage, javaPackage).getReference());
            }
        }

        return packages;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

}
