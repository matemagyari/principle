package org.tindalos.principle.internal.domain.core;

/**
 * Represents the result of an architectural analysis.
 * Implementations provide specific details about what was analyzed
 * and whether constraints were violated.
 */
public interface AnalysisResult {

    /**
     * Determines whether the architectural constraint was violated.
     *
     * @return true if the constraint was violated, false otherwise
     */
    boolean constraintViolated();

}

