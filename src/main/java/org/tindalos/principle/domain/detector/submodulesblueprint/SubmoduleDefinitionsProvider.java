package org.tindalos.principle.domain.detector.submodulesblueprint;

import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;

public interface SubmoduleDefinitionsProvider {
    
    SubmoduleDefinitions readSubmoduleDefinitions(SubmodulesDefinitionLocation submodulesDefinitionLocation);

}
