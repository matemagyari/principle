package org.tindalos.principle.domain.detector.adp;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageSorter;

public class PackageStructureBuilder {
	
	private final PackageSorter packageSorter;
	
	public PackageStructureBuilder(PackageSorter packageSorter) {
		this.packageSorter = packageSorter;
	}

	public Package build(List<Package> packages, String basePackageName) {
		List<Package> sortedPackages = packageSorter.sortByName(packages, basePackageName);
		Package basePackage = sortedPackages.remove(0);
		for (Package aPackage : sortedPackages) {
			basePackage.insert(aPackage);
		}
		return basePackage;
	}

}
