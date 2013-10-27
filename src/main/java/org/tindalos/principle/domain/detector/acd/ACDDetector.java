package org.tindalos.principle.domain.detector.acd;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
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
		
		int numOfPackages = basePackage.toMap().size();
		
		System.err.println("NPAC 1 " + packages.size() + " NPAC 2 " + numOfPackages);
		
		int cumulatedComponentDependency = 0;
		for (Package aPackage : basePackage.toMap().values()) {
			int calculateNumberOfCumulatedDependees = aPackage.calculateNumberOfCumulatedDependees();
			System.err.println(aPackage + " " + calculateNumberOfCumulatedDependees);
			cumulatedComponentDependency += calculateNumberOfCumulatedDependees;
		}
		
		Double acd = (double) cumulatedComponentDependency / (double) packages.size();
		System.err.println(acd);
		return new ACDResult(acd);
	}

}
