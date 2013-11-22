package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder;
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;

public class SubmodulesFactory {
    
    private final PackageStructureBuilder packageStructureBuilder;
    private final SubmoduleDefinitionsProvider submoduleDefinitionsProvider;
    private final SubmoduleFactory submoduleFactory;
    

    public SubmodulesFactory(PackageStructureBuilder packageStructureBuilder,
            SubmoduleDefinitionsProvider submoduleDefinitionsProvider, SubmoduleFactory submoduleFactory) {
        this.packageStructureBuilder = packageStructureBuilder;
        this.submoduleDefinitionsProvider = submoduleDefinitionsProvider;
        this.submoduleFactory = submoduleFactory;
    }

    public List<Submodule> buildSubmodules(SubmodulesDefinitionLocation submodulesDefinitionLocation,
            List<Package> packages, String basePackageName) {
       
        SubmoduleDefinitions submoduleDefinitions = submoduleDefinitionsProvider.readSubmoduleDefinitions(submodulesDefinitionLocation);
        Package basePackage = packageStructureBuilder.build(packages, basePackageName);

        return submoduleFactory.buildModules(submoduleDefinitions, basePackage.toMap());
    }

}
