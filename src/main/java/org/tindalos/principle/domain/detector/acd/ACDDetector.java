package org.tindalos.principle.domain.detector.acd;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;

public class ACDDetector implements Detector {
	
	private final PackageStructureBuilder packageStructureBuilder;

	public ACDDetector(PackageStructureBuilder packageStructureBuilder) {
		this.packageStructureBuilder = packageStructureBuilder;
	}

	public CheckResult analyze(CheckInput checkInput) {
		List<Package> packages = checkInput.getPackages();
		Package basePackage = packageStructureBuilder.build(packages, checkInput.getConfiguration().getBasePackage());
		
		Map<PackageReference, Package> referenceMap = basePackage.toMap();
		referenceMap.remove(basePackage.getReference());
		
		//System.err.println("NPAC 1 " + packages.size() + " NPAC 2 " + referenceMap.size());
		
		//either the packages that actually have their own references, or all the packages
		Collection<Package> relevantPackages = packages;
		
		int cumulatedComponentDependency = 0;
		for (Package aPackage : relevantPackages) {
			int calculateNumberOfCumulatedDependees = aPackage.cumulatedDependencies2(referenceMap).size() + 1;
			//System.err.println(aPackage + " " + calculateNumberOfCumulatedDependees);
			cumulatedComponentDependency += calculateNumberOfCumulatedDependees;
		}
		
		return new ACDResult(cumulatedComponentDependency,relevantPackages.size(), checkInput.getPackageCouplingExpectations());
	}

	public boolean isWanted(DesignQualityExpectations expectations) {
		PackageCoupling packageCoupling = expectations.getPackageCoupling();
		if (packageCoupling == null) {
			return false;
		}
		return packageCoupling.getACD() != null 
				|| packageCoupling.getRACD() != null
				|| packageCoupling.getNCCD() != null;
	}
	

}
