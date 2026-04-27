package org.tindalos.principle.internal.domain.core;

/**
 * Exception thrown when domain rules or constraints are violated.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

}

