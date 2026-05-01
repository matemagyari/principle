package org.tindalos.guardrails.internal.infrastructure.readers;

import org.tindalos.guardrails.internal.domain.core.Constraint;

import java.util.Map;
import java.util.Optional;

public interface ConstraintDefinitionReader<T extends Constraint> {

    Optional<T> read(Map<String, Object> yamlObject);
}