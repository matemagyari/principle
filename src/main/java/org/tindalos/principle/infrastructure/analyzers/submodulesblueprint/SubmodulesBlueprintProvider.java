package org.tindalos.principle.infrastructure.analyzers.submodulesblueprint;

import org.tindalos.principle.domain.constraints.submodules.SubmoduleDefinitions;

public interface SubmodulesBlueprintProvider {
    SubmoduleDefinitions readSubmoduleDefinitions(String basePackageName, String submodulesDefinitionLocation, int violationThreshold);
}
