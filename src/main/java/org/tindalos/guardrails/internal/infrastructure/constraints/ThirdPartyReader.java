package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.infrastructure.readers.ConstraintDefinitionReader;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThirdPartyReader implements ConstraintDefinitionReader<ThirdParty> {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ThirdParty> read(Map<String, Object> yamlObject) {
        return Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"))
                .map(section -> (Map<String, Object>) section.get("third_party_restrictions"))
                .map(structure -> {
                    var barriersYaml = (List<Map<String, Object>>) structure.get("allowed_libraries");
                    var barriers = barriersYaml.stream()
                            .map(m -> new Barrier(
                                    (String) m.get("layer"),
                                    (List<String>) m.get("libraries")))
                            .toList();
                    return new ThirdParty(barriers, (Integer) structure.get("violation_threshold"));
                });
    }
}
