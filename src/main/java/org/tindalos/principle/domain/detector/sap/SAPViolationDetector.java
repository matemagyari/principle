package org.tindalos.principle.domain.detector.sap;

import java.util.List;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.SAP;

import com.google.common.collect.Lists;

public class SAPViolationDetector implements Detector {

	public CheckResult analyze(CheckInput checkInput) {
		List<Package> outlierPackages = Lists.newArrayList();
		SAP sapExpectation = checkInput.getPackageCouplingExpectations().getSAP();
		Double maxDistance = sapExpectation.getMaxDistance();
		
		List<Package> packages = checkInput.getPackages();
		removeRootPackageIfEmpty(packages);

		for (Package aPackage : packages) {
			
			if (aPackage.distance() > maxDistance) {
				outlierPackages.add(aPackage);
			}
		}
		return new SAPResult(outlierPackages, sapExpectation);
	}

	private static void removeRootPackageIfEmpty(List<Package> packages) {
		Package rootPackage = packages.get(0);
		Metrics metrics = rootPackage.getMetrics();
		if (metrics.getAbstractness() == 0 && metrics.getInstability() == 0) {
			packages.remove(rootPackage);
		}
	}
	
	public boolean isWanted(DesignQualityExpectations expectations) {
		PackageCoupling packageCoupling = expectations.getPackageCoupling();
		if (packageCoupling == null) {
			return false;
		}
		return packageCoupling.getSAP() != null;
	}

}
