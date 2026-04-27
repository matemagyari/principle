package org.tindalos.principle.internal.domain.constraints.exception;

/**
 * Exception thrown when the configuration for architectural constraints is invalid.
 * This includes issues like invalid layer definitions, missing required fields,
 * or contradictory constraint specifications.
 */
public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}

