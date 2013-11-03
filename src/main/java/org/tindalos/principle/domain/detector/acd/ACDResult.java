package org.tindalos.principle.domain.detector.acd;

import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.cumulativedependency.DoubleThresholder;

public class ACDResult implements CheckResult {

    private final Double acd;
    private final Double rAcd;
    private final Double nCcd;
	private final PackageCoupling packageCouplingExpectation;

    public ACDResult(int cumulatedComponentDependency, int numOfComponents, PackageCoupling packageCoupling) {
        this.packageCouplingExpectation = packageCoupling;
		this.acd = (double) cumulatedComponentDependency / (double) numOfComponents;
        this.rAcd = this.acd / (double) numOfComponents;
        this.nCcd = this.acd / (double) numOfComponents;
    }

    public Double getACD() {
		return acd;
	}
    
    public Double getRACD() {
		return rAcd;
	}
    
    public Double getNCCD() {
		return nCcd;
	}
    
	public boolean expectationsFailed() {
		return greaterIfExists(acd, packageCouplingExpectation.getACD())
				|| greaterIfExists(rAcd, packageCouplingExpectation.getRACD())
				|| greaterIfExists(nCcd, packageCouplingExpectation.getNCCD());
	}

	private boolean greaterIfExists(Double actual, DoubleThresholder expectation) {
		if (expectation == null || expectation.getThreshold() == null) {
			return false;
		}
		return actual > expectation.getThreshold();
	}



}
