package org.tindalos.guardrails.internal.domain.constraints;

import org.tindalos.guardrails.internal.domain.core.Constraint;

public interface IntConstraint extends Constraint {

    int violationThreshold();
}
