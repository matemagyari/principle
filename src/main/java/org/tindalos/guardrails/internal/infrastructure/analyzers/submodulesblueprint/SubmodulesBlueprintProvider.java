package org.tindalos.guardrails.internal.infrastructure.analyzers.submodulesblueprint;

import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinitions;

import java.util.Map;

public interface SubmodulesBlueprintProvider {
    SubmoduleDefinitions readSubmoduleDefinitions(Map<String, Object> yamlObject);
}
