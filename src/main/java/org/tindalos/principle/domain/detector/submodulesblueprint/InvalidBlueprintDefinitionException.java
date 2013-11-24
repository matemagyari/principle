package org.tindalos.principle.domain.detector.submodulesblueprint;

import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException;

@SuppressWarnings("serial")
public class InvalidBlueprintDefinitionException extends InvalidConfigurationException {

	public InvalidBlueprintDefinitionException(String msg) {
		super(msg);
	}
}
