package org.tindalos.guardrails.internal.infrastructure.constraints;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.infrastructure.core.ConstraintDefinitionReader;

public class ThirdPartyReader implements ConstraintDefinitionReader<ThirdParty> {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ThirdParty> read(Map<String, Object> yamlObject) {
        return Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"))
                .map(section -> (Map<String, Object>) section.get("third_party_restrictions"))
                .map(structure -> {
                    var barriersYaml = (List<Map<String, Object>>) structure.get("allowed_libraries");
                    var barriers = barriersYaml.stream()
                            .flatMap(m -> m.entrySet().stream()
                                    .map(entry -> new Barrier(
                                            entry.getKey(),
                                            (List<String>) entry.getValue())))
                            .toList();
                    return new ThirdParty(barriers, (Integer) structure.get("violation_threshold"));
                });
    }
}
