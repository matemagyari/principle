package org.principle.domain.detector.cycledetector.jdepend;

import java.util.Collection;
import java.util.Set;

import jdepend.framework.JavaPackage;

import org.principle.domain.detector.cycledetector.core.Package;
import org.principle.domain.detector.cycledetector.core.PackageReference;

import com.google.common.collect.Sets;

public class LazyLoadingJDependBasedPackage extends Package {
	
    private final JavaPackage javaPackage;
    private final String basePackage;

	public LazyLoadingJDependBasedPackage(JavaPackage javaPackage, String basePackage) {
        super(javaPackage.getName());
		this.javaPackage = javaPackage;
        this.basePackage = basePackage;
    }

    @SuppressWarnings("unchecked")
    protected Set<PackageReference> getOwnPackageReferences() {

        Set<PackageReference> packages = Sets.newHashSet();

        Collection<JavaPackage> efferents = javaPackage.getEfferents();
        for (JavaPackage javaPackage : efferents) {
            if (javaPackage.getName().startsWith(basePackage)) {
                packages.add(new LazyLoadingJDependBasedPackage(javaPackage, basePackage).getReference());
            }
        }

        return packages;
    }

}
