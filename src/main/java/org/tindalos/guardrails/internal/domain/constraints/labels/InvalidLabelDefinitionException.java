package org.tindalos.guardrails.internal.domain.constraints.labels;

import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;

/**
 * Exception thrown when a label definition is invalid.
 */
public class InvalidLabelDefinitionException extends InvalidConfigurationException {
    public InvalidLabelDefinitionException(String message) {
        super(message);
    }
}
