package org.tindalos.principle.internal.domain.constraints.submodules;

import org.tindalos.principle.internal.domain.constraints.exception.InvalidConfigurationException;

/**
 * Exception thrown when a submodule blueprint definition is invalid.
 * This includes issues like overlapping submodules, circular dependencies between submodules,
 * or invalid module structure definitions.
 */
public class InvalidBlueprintDefinitionException extends InvalidConfigurationException {

    public InvalidBlueprintDefinitionException(String message) {
        super(message);
    }

}

