package org.tindalos.principle.domain.expectations;

public class SubmodulesBlueprint extends Thresholder {

	private String location;

	public SubmodulesBlueprint(SubmodulesDefinitionLocation submodulesDefinitionLocation, Integer threshold) {
		this.location = submodulesDefinitionLocation.filePath();
		this.violationsThreshold = threshold;
	}

	public SubmodulesBlueprint() {
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public SubmodulesDefinitionLocation getSubmodulesDefinitionLocation() {
		return new SubmodulesDefinitionLocation(location);
	}

}
