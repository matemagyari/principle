package org.tindalos.principle.domain.core;

import org.tindalos.principle.domain.core.checkerparameter.DesingQualityCheckParameters;

public class DesignQualityCheckParameters {
	
	private final DesingQualityCheckParameters checks;
	private final String basePackage;
	
	public DesignQualityCheckParameters(DesingQualityCheckParameters checks, String basePackage) {
		this.checks = checks;
		this.basePackage = basePackage;
	}
	
	public DesingQualityCheckParameters getChecks() {
		return checks;
	}
	
	public String getBasePackage() {
		return basePackage;
	}
}
