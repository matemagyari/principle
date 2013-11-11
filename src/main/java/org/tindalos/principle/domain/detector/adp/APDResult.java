package org.tindalos.principle.domain.detector.adp;

import java.util.Collections;
import java.util.List;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.ADP;

import com.google.common.collect.Lists;

public class APDResult implements CheckResult {

    private final List<Cycle> cycles;
	private final ADP expectation;

    public APDResult(List<Cycle> cycles, ADP adpExpectation) {
        this.expectation = adpExpectation;
		this.cycles = Lists.newArrayList(cycles);
		Collections.sort(this.cycles);
    }
    
    public List<Cycle> getCycles() {
        return cycles;
    }

	public boolean expectationsFailed() {
		return cycles.size() > expectation.getViolationsThreshold();
	}

    public Integer getThreshold() {
        return expectation.getViolationsThreshold();
    }

}
