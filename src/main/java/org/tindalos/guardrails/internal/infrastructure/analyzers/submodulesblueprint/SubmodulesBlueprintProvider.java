package org.tindalos.guardrails.internal.infrastructure.analyzers.submodulesblueprint;

import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinitions;

public interface SubmodulesBlueprintProvider {
    SubmoduleDefinitions readSubmoduleDefinitions(String basePackageName, String submodulesDefinitionLocation, int violationThreshold);
}
