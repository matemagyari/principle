package org.tindalos.principle.domain.detector.adp;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageSorter;
import org.tindalos.principle.domain.coredetector.DesignQualityCheckParameters;

public class PackageStructureBuilder {
	
	private final PackageSorter packageSorter;
	
	public PackageStructureBuilder(PackageSorter packageSorter) {
		this.packageSorter = packageSorter;
	}

	public Package build(List<Package> packages, DesignQualityCheckParameters parameters) {
		List<Package> sortedPackages = packageSorter.sortByName(packages, parameters.getBasePackage());
		Package basePackage = sortedPackages.remove(0);
		for (Package aPackage : sortedPackages) {
			basePackage.insert(aPackage);
		}
		return basePackage;
	}

}
