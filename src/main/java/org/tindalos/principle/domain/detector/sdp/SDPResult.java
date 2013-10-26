package org.tindalos.principle.domain.detector.sdp;

import java.util.List;

import org.tindalos.principle.domain.detector.core.CheckResult;

import com.google.common.collect.Lists;

public class SDPResult implements CheckResult {

	private final List<SDPViolation> violations;
	
	public SDPResult(List<SDPViolation> violations) {
		this.violations = Lists.newArrayList(violations);
	}

	public String detectorId() {
		return null;
	}

	public boolean violationsDetected() {
		return !violations.isEmpty();
	}

	public int numberOfViolations() {
		return violations.size();
	}
	
	public List<SDPViolation> getViolations() {
		return Lists.newArrayList(violations);
	}

}
