package org.tindalos.principle.infrastructure;

import org.tindalos.principle.domain.detector.submoduleboundaries.SubmoduleDefinitions;
import org.tindalos.principle.domain.detector.submoduleboundaries.SubmoduleDefinitionsProvider;
import org.tindalos.principle.domain.detector.submoduleboundaries.SubmoduleDependenciesBlueprint;
import org.tindalos.principle.domain.detector.submoduleboundaries.SubmoduleDependenciesBlueprintProvider;
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;

public class SubmoduleXXXProvider implements SubmoduleDefinitionsProvider, SubmoduleDependenciesBlueprintProvider {


    public SubmoduleDependenciesBlueprint readSubmoduleDependenciesBlueprint(
            SubmodulesDefinitionLocation submodulesDefinitionLocation) {
        throw new org.apache.commons.lang.NotImplementedException("TODO mmagyari");
    }

    public SubmoduleDefinitions readSubmoduleDefinitions(SubmodulesDefinitionLocation submodulesDefinitionLocation) {
        throw new org.apache.commons.lang.NotImplementedException("TODO mmagyari");
    }

}
