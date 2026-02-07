package org.tindalos.principle.domain.expectations;

/**
 * Interface for thresholders that use a double value as threshold.
 * Represents expectations with floating-point threshold values.
 */
public interface DoubleThresholder {

    /**
     * Gets the threshold value.
     *
     * @return the threshold value as a double
     */
    double threshold();
}
