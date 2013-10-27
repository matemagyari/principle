package org.tindalos.principle.domain.checkerparameter;

public abstract class Thresholders {

	//default threshold
	protected Integer violationsThreshold = 0;
	
	public Integer getViolationsThreshold() {
		return violationsThreshold;
	}
	
	public void setViolationsThreshold(Integer violationsThreshold) {
		this.violationsThreshold = violationsThreshold;
	}
}
