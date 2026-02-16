package org.tindalos.principle.domain.constraints;

/**
 * Interface for thresholders that use a double value as threshold.
 * Represents constraints with floating-point threshold values.
 */
public interface DoubleExpectation {

    double threshold();
}
