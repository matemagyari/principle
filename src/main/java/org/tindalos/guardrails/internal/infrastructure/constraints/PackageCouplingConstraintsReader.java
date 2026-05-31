package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.tindalos.guardrails.internal.domain.constraints.ADP;
import org.tindalos.guardrails.internal.domain.constraints.Grouping;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.RACD;
import org.tindalos.guardrails.internal.domain.constraints.SAP;
import org.tindalos.guardrails.internal.domain.constraints.SDP;
import org.tindalos.guardrails.internal.infrastructure.core.ConstraintDefinitionReader;

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
                            .map(t -> new RACD(asDouble(t)))
                            .ifPresent(builder::racd);
                    Optional.ofNullable(structure.get("cyclic_dependencies_threshold"))
                            .map(t -> new ADP(asInt(t)))
                            .ifPresent(builder::adp);
                    Optional.ofNullable((Map<String, Object>) structure.get("sdp"))
                            .map(sdp -> new SDP(asInt(sdp.getOrDefault("violation_threshold", 0))))
                            .ifPresent(builder::sdp);
                    Optional.ofNullable((Map<String, Object>) structure.get("sap"))
                            .map(sap -> new SAP(
                                    asInt(sap.getOrDefault("violation_threshold", 0)),
                                    asDouble(sap.getOrDefault("max_distance", 0.0))))
                            .ifPresent(builder::sap);
                    Optional.ofNullable(structure.get("structure_analysis_enabled"))
                            .filter(Boolean.class::isInstance)
                            .map(Boolean.class::cast)
                            .filter(Boolean::booleanValue)
                            .map(ignored -> Grouping.of())
                            .ifPresent(builder::grouping);
                    return builder.build();
                });
    }

    private static int asInt(Object value) {
        return ((Number) value).intValue();
    }

    private static double asDouble(Object value) {
        return ((Number) value).doubleValue();
    }
}
