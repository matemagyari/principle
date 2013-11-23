package org.tindalos.principle.domain.expectations;

public class SubmodulesDefinitionLocation {
	
	private final String filePath;

	public SubmodulesDefinitionLocation(String filePath) {
		this.filePath = filePath;
	}

	public String filePath() {
		return filePath;
	}

}
