package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.tindalos.guardrails.internal.domain.constraints.Layering;
import org.tindalos.guardrails.internal.infrastructure.readers.ConstraintDefinitionReader;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LayeringReader implements ConstraintDefinitionReader<Layering> {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Layering> read(Map<String, Object> yamlObject) {
        return Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"))
                .map(section -> (Map<String, Object>) section.get("layering"))
                .map(structure -> {
                    var layers = Optional.ofNullable((List<String>) structure.get("layers")).orElse(List.of());
                    var threshold = Optional.ofNullable((Integer) structure.get("violation_threshold")).orElse(0);
                    return new Layering(layers, threshold);
                });
    }
}
