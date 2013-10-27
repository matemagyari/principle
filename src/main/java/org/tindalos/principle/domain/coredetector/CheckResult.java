package org.tindalos.principle.domain.coredetector;

public interface CheckResult {
	
	String detectorId();
	
	boolean violationsDetected();
	
	int numberOfViolations();
	
}
