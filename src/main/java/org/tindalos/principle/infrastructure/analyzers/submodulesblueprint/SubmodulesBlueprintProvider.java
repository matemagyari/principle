package org.tindalos.principle.infrastructure.analyzers.submodulesblueprint;

import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmoduleDefinitions;

public interface SubmodulesBlueprintProvider {
    SubmoduleDefinitions readSubmoduleDefinitions(String basePackageName, String submodulesDefinitionLocation);
}
