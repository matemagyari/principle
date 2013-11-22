package org.tindalos.principle.domain.detector.submoduleboundaries.xxx;

import java.util.List;

import org.tindalos.principle.domain.detector.submoduleboundaries.Submodule;
import org.tindalos.principle.domain.detector.submoduleboundaries.SubmoduleDependenciesBlueprint;

public class SystemDecomposition {
    
    private final List<Submodule> submodules;
    private final SubmoduleDependenciesBlueprint submoduleDependenciesBlueprint;

    public SystemDecomposition(SubmoduleDependenciesBlueprint submoduleDependenciesBlueprint, List<Submodule> submodules) {
        this.submoduleDependenciesBlueprint = submoduleDependenciesBlueprint;
        this.submodules = submodules;
    }  
    
    public SubmoduleDependenciesBlueprint getSubmoduleDependenciesBlueprint() {
        return submoduleDependenciesBlueprint;
    }
    
    public List<Submodule> getSubmodules() {
        return submodules;
    }

}
