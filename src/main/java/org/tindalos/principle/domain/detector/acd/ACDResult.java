package org.tindalos.principle.domain.detector.acd;

import org.tindalos.principle.domain.coredetector.CheckResult;

public class ACDResult implements CheckResult {

    private final Double acd;
    private final Double rAcd;
    private final Double nCcd;

    public ACDResult(int cumulatedComponentDependency, int numOfComponents) {
        this.acd = (double) cumulatedComponentDependency / (double) numOfComponents;
        this.rAcd = this.acd / (double) numOfComponents;
        this.nCcd = this.acd / (double) numOfComponents;
    }

    public boolean violationsDetected() {
        return false;
    }

    public int numberOfViolations() {
        return 0;
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

}
