package org.tindalos.principle.domain.core;

@SuppressWarnings("serial")
public class DomainException extends RuntimeException {

    public DomainException(String msg) {
        super(msg);
    }

}
