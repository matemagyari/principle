package org.tindalos.principle.domain.detector.sap;

import java.util.List;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;

import com.google.common.collect.Lists;

public class SAPViolationDetector implements Detector {

	public CheckResult analyze(CheckInput checkInput) {
		List<Package> outlierPackages = Lists.newArrayList();
		Double maxDistance = checkInput.getParameters().getChecks().getPackageCoupling().getSAP().getMaxDistance();
		
		List<Package> packages = checkInput.getPackages();
		removeRootPackageIfEmpty(packages);

		for (Package aPackage : packages) {
			
			if (aPackage.distance() > maxDistance) {
				outlierPackages.add(aPackage);
			}
		}
		return new SAPResult(outlierPackages);
	}

	private static void removeRootPackageIfEmpty(List<Package> packages) {
		Package rootPackage = packages.get(0);
		Metrics metrics = rootPackage.getMetrics();
		if (metrics.getAbstractness() == 0 && metrics.getInstability() == 0) {
			packages.remove(rootPackage);
		}
	}

}
