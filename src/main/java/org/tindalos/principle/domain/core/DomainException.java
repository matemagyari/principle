package org.tindalos.principle.domain.core;

/**
 * Exception thrown when domain rules or constraints are violated.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

}

