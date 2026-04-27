package org.tindalos.principle.internal.infrastructure.analyzers.submodulesblueprint;

import org.tindalos.principle.internal.domain.constraints.submodules.SubmoduleDefinitions;

public interface SubmodulesBlueprintProvider {
    SubmoduleDefinitions readSubmoduleDefinitions(String basePackageName, String submodulesDefinitionLocation, int violationThreshold);
}
