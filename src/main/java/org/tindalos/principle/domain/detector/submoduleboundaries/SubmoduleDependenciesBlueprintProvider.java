package org.tindalos.principle.domain.detector.submoduleboundaries;

import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;

public interface SubmoduleDependenciesBlueprintProvider {
    
    SubmoduleDependenciesBlueprint readSubmoduleDependenciesBlueprint(SubmodulesDefinitionLocation submodulesDefinitionLocation);

}
