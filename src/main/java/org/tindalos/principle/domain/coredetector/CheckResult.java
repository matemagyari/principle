package org.tindalos.principle.domain.coredetector;

public interface CheckResult {
	
	boolean violationsDetected();
	
	int numberOfViolations();
	
}
