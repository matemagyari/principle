package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.tindalos.guardrails.internal.domain.constraints.ADP;
import org.tindalos.guardrails.internal.domain.constraints.Grouping;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.RACD;
import org.tindalos.guardrails.internal.infrastructure.readers.ConstraintDefinitionReader;

import java.util.Map;
import java.util.Optional;

public class PackageCouplingConstraintsReader implements ConstraintDefinitionReader<PackageCouplingConstraints> {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<PackageCouplingConstraints> read(Map<String, Object> yamlObject) {
        return Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"))
                .map(section -> (Map<String, Object>) section.get("package_coupling"))
                .map(structure -> {
                    var builder = PackageCouplingConstraints.builder();
                    Optional.ofNullable(structure.get("acd_threshold"))
                            .map(t -> new RACD((Double) t))
                            .ifPresent(builder::racd);
                    Optional.ofNullable(structure.get("cyclic_dependencies_threshold"))
                            .map(t -> new ADP((Integer) t))
                            .ifPresent(builder::adp);
                    Optional.ofNullable(structure.get("structure_analysis_enabled"))
                            .filter(Boolean.class::isInstance)
                            .map(Boolean.class::cast)
                            .filter(Boolean::booleanValue)
                            .map(ignored -> Grouping.of())
                            .ifPresent(builder::grouping);
                    return builder.build();
                });
    }
}
