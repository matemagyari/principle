package org.tindalos.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.detector.core.PackageSorter;

import com.google.common.collect.Lists;

public class PackageListFactory {

	private final PackageFactory packageFactory;
	private final PackageSorter packageSorter;

	public PackageListFactory(PackageFactory packageFactory, PackageSorter packageSorter) {
		this.packageFactory = packageFactory;
		this.packageSorter = packageSorter;
	}

	public List<Package> build(Collection<JavaPackage> analyzedPackages) {

		List<Package> result = Lists.newArrayList();
		for (JavaPackage javaPackage : analyzedPackages) {
			if (packageFactory.isRelevant(javaPackage)) {
				Package aPackage = packageFactory.transform(javaPackage);
				result.add(aPackage);
			}
		}
		return packageSorter.sortByName(result);
	}

}
