package org.tindalos.principle.domain.core;

import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.Layering;

public class DesignQualityCheckConfiguration {
	
	private final DesignQualityExpectations expectations;
	private final String basePackage;
	
	public DesignQualityCheckConfiguration(DesignQualityExpectations expectations, String basePackage) {
		this.expectations = expectations;
		this.basePackage = basePackage;
	}
	
	public DesignQualityExpectations getExpectations() {
		return expectations;
	}
	
	public Layering getLayeringExpectations() {
		return this.expectations.getLayering();
	}
	
	public String getBasePackage() {
		return basePackage;
	}
}
