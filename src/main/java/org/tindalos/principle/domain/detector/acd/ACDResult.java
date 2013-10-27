package org.tindalos.principle.domain.detector.acd;

import java.util.ArrayList;
import java.util.List;

import org.tindalos.principle.domain.coredetector.CheckResult;

import com.google.common.collect.Lists;

public class ACDResult implements CheckResult {

    private final List<Package> outlierPackages;
    private final Double acd;

    public ACDResult(List<Package> outlierPackages, Double acd) {
        this.acd = acd;
		this.outlierPackages = Lists.newArrayList(outlierPackages);
    }
    public ACDResult( Double acd) {
        this(new ArrayList<Package>(), acd);
    }    

	public String detectorId() {
		return "";
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

}
