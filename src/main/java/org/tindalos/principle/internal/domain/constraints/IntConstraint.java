package org.tindalos.principle.internal.domain.constraints;

import org.tindalos.principle.internal.domain.core.Constraint;

public interface IntConstraint extends Constraint {

    int violationThreshold();
}
