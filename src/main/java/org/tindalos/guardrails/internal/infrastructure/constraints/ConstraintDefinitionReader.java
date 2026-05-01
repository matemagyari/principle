package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.tindalos.guardrails.internal.domain.core.Constraint;

public interface ConstraintDefinitionReader<T extends Constraint> {

    T read(String definitions);
}
