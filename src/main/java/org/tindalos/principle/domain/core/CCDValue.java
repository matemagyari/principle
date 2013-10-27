package org.tindalos.principle.domain.core;

public class CCDValue {
	
	private final Integer componentDependency;
	private final Integer cumulatedComponentDependency;
	
	public CCDValue(Integer componentDependency, Integer cumulatedComponentDependency) {
		this.componentDependency = componentDependency;
		this.cumulatedComponentDependency = cumulatedComponentDependency;
	}
	
	public Integer getComponentDependency() {
		return componentDependency;
	}
	public Integer getCumulatedComponentDependency() {
		return cumulatedComponentDependency;
	}

	@Override
	public String toString() {
		return "CCDValue [componentDependency=" + componentDependency + ", cumulatedComponentDependency=" + cumulatedComponentDependency + "]";
	}
}
