package org.tindalos.principle.domain.core;

import org.tindalos.principle.domain.core.checkerparameter.Checks;

public class DesignQualityCheckParameters {
	
	private final Checks checks;
	private final String basePackage;
	
	public DesignQualityCheckParameters(Checks checks, String basePackage) {
		this.checks = checks;
		this.basePackage = basePackage;
	}
	
	public Checks getChecks() {
		return checks;
	}
	
	public String getBasePackage() {
		return basePackage;
	}
}
