package org.tindalos.guardrails.internal.domain.constraints.slices;

import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;

/**
 * Exception thrown when a slice definition is invalid.
 */
public class InvalidSliceDefinitionException extends InvalidConfigurationException {
    public InvalidSliceDefinitionException(String message) {
        super(message);
    }
}
