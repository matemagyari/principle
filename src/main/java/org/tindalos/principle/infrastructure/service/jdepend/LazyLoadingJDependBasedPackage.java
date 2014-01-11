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
    private final PackageFactory packageFactory;
    private final Metrics metrics;
	private final boolean isUnreferred;

	public LazyLoadingJDependBasedPackage(JavaPackage javaPackage, Metrics metrics, PackageFactory packageFactory) {
        super(javaPackage.getName());
		this.javaPackage = javaPackage;
        this.metrics = metrics;
        this.packageFactory = packageFactory;
        this.isUnreferred = metrics.afferentCoupling() == 0;
        //System.err.println(javaPackage.getName() + " " + metrics);
    }

    @SuppressWarnings("unchecked")
    public Set<PackageReference> getOwnPackageReferences() {

        Set<PackageReference> packages = Sets.newHashSet();

        Collection<JavaPackage> efferents = javaPackage.getEfferents();
        for (JavaPackage javaPackage : efferents) {
            if (packageFactory.isRelevant(javaPackage)) {
                packages.add(packageFactory.transform(javaPackage).getReference());
            }
        }

        return packages;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

	@Override
	public boolean isUnreferred() {
		return this.isUnreferred;
	}

}
