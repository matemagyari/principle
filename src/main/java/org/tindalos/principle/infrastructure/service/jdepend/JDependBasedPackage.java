package org.tindalos.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.Set;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Sets;

public class JDependBasedPackage extends Package {
	
    private Set<PackageReference> ownPackageReferences;
    private final Metrics metrics;
	private final boolean isUnreferred;

	public JDependBasedPackage(JavaPackage javaPackage, String basePackage, Metrics metrics) {
        super(javaPackage.getName());
        this.metrics = metrics;
        this.isUnreferred = metrics.getAfferentCoupling() == 0;
        this.ownPackageReferences =  createOwnPackageReferences(javaPackage, basePackage);
    }
	
	@Override
	public Set<PackageReference> getOwnPackageReferences() {
		return ownPackageReferences;
	}

    @SuppressWarnings("unchecked")
    protected Set<PackageReference> createOwnPackageReferences(JavaPackage theJavaPackage, String basePackage) {

        Set<PackageReference> packages = Sets.newHashSet();

        Collection<JavaPackage> efferents = theJavaPackage.getEfferents();
        for (JavaPackage javaPackage : efferents) {
            if (javaPackage.getName().startsWith(basePackage)) {
                packages.add(new PackageReference(javaPackage.getName()));
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
