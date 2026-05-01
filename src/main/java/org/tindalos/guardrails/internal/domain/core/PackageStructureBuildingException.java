package org.tindalos.guardrails.internal.domain.core;

/**
 * Thrown when an error occurs while building the package structure hierarchy.
 */
public class PackageStructureBuildingException extends RuntimeException {

    public PackageStructureBuildingException(String message) {
        super(message);
    }
}

