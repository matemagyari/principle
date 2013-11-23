package org.tindalos.principle.domain.expectations;

public abstract class Thresholder {

	//default threshold
	protected Integer violationsThreshold = 0;
	
	public Integer getViolationsThreshold() {
		return violationsThreshold;
	}
	
	public void setViolationsThreshold(Integer violationsThreshold) {
		this.violationsThreshold = violationsThreshold;
	}
}
