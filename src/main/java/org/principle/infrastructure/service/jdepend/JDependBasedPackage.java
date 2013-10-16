package org.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.Set;

import jdepend.framework.JavaPackage;

import org.principle.domain.core.Package;
import org.principle.domain.core.PackageReference;

import com.google.common.collect.Sets;

public class JDependBasedPackage extends Package {
	
    private Set<PackageReference> ownPackageReferences;

	public JDependBasedPackage(JavaPackage javaPackage, String basePackage) {
        super(javaPackage.getName());
        this.ownPackageReferences =  createOwnPackageReferences(javaPackage, basePackage);
    }
	
	@Override
	public Set<PackageReference> getOwnPackageReferences() {
		return ownPackageReferences;
	}

    @SuppressWarnings("unchecked")
    protected static Set<PackageReference> createOwnPackageReferences(JavaPackage theJavaPackage, String basePackage) {

        Set<PackageReference> packages = Sets.newHashSet();

        Collection<JavaPackage> efferents = theJavaPackage.getEfferents();
        for (JavaPackage javaPackage : efferents) {
            if (javaPackage.getName().startsWith(basePackage)) {
                packages.add(new PackageReference(javaPackage.getName()));
            }
        }

        return packages;
    }

}
