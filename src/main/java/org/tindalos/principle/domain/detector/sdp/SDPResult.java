package org.tindalos.principle.domain.detector.sdp;

import java.util.List;

import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.SDP;

import com.google.common.collect.Lists;

public class SDPResult implements CheckResult {

	private final List<SDPViolation> violations;
	private final SDP expectation;
	
	public SDPResult(List<SDPViolation> violations, SDP expectation) {
		this.expectation = expectation;
		this.violations = Lists.newArrayList(violations);
	}

	public List<SDPViolation> getViolations() {
		return Lists.newArrayList(violations);
	}

	public boolean expectationsFailed() {
		return violations.size() > expectation.getViolationsThreshold();
	}
	

    public Integer getThreshold() {
        return expectation.getViolationsThreshold();
    }

}
