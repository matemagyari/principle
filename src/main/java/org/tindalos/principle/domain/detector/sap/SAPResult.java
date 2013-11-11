package org.tindalos.principle.domain.detector.sap;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.SAP;
import org.tindalos.principle.infrastructure.service.jdepend.PackageFactory;

import com.google.common.collect.Lists;

public class SAPResult implements CheckResult {
	PackageFactory ccc;
	
	private final List<Package> outlierPackages;
	private final SAP sapExpectation;
	
	public SAPResult(List<Package> outlierPackages, SAP sapExpectation) {
		this.sapExpectation = sapExpectation;
		this.outlierPackages = Lists.newArrayList(outlierPackages);
	}

	public List<Package> getOutlierPackages() {
		return Lists.newArrayList(outlierPackages);
	}

	public boolean expectationsFailed() {
		return outlierPackages.size() > sapExpectation.getViolationsThreshold();
	}

    public Integer getThreshold() {
        return sapExpectation.getViolationsThreshold();
    }

}
