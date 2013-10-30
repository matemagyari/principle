package org.tindalos.principle.domain.detector.acd;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder;

public class ACDDetector implements Detector {
	
	private final PackageStructureBuilder packageStructureBuilder;

	public ACDDetector(PackageStructureBuilder packageStructureBuilder) {
		this.packageStructureBuilder = packageStructureBuilder;
	}

	public CheckResult analyze(CheckInput checkInput) {
		List<Package> packages = checkInput.getPackages();
		Package basePackage = packageStructureBuilder.build(packages, checkInput.getParameters());
		
		Map<PackageReference, Package> referenceMap = basePackage.toMap();
		referenceMap.remove(basePackage.getReference());
		
		//System.err.println("NPAC 1 " + packages.size() + " NPAC 2 " + referenceMap.size());
		
		int cumulatedComponentDependency = 0;
		for (Package aPackage : referenceMap.values()) {
			int calculateNumberOfCumulatedDependees = aPackage.cumulatedDependencies2(referenceMap).size() + 1;
			//System.err.println(aPackage + " " + calculateNumberOfCumulatedDependees);
			cumulatedComponentDependency += calculateNumberOfCumulatedDependees;
		}
		
		return new ACDResult(cumulatedComponentDependency,referenceMap.size() );
	}
	

}
