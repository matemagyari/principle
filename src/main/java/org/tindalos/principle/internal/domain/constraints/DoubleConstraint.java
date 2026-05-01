package org.tindalos.principle.internal.domain.constraints;

import org.tindalos.principle.internal.domain.core.Constraint;

/**
 * Interface for thresholders that use a double value as threshold.
 * Represents constraints with floating-point threshold values.
 */
public interface DoubleConstraint extends Constraint {

    double threshold();
}
