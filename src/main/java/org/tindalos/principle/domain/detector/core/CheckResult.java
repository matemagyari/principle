package org.tindalos.principle.domain.detector.core;

public interface CheckResult {
	
	String detectorId();
	
	boolean violationsDetected();
	
	int numberOfViolations();
	
}
