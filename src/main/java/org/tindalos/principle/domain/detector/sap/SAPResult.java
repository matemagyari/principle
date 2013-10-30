package org.tindalos.principle.domain.detector.sap;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.CheckResult;

import com.google.common.collect.Lists;

public class SAPResult implements CheckResult {
	
	private final List<Package> outlierPackages;
	
	public SAPResult(List<Package> outlierPackages) {
		this.outlierPackages = Lists.newArrayList(outlierPackages);
	}

	public boolean violationsDetected() {
		return !outlierPackages.isEmpty();
	}

	public int numberOfViolations() {
		return outlierPackages.size();
	}
	
	public List<Package> getOutlierPackages() {
		return Lists.newArrayList(outlierPackages);
	}

}
