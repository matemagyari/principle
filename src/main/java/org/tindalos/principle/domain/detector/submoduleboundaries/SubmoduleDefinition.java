package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.Set;

import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Sets;

public class SubmoduleDefinition {
    
    private final SubmoduleId id;
    private final Set<PackageReference> packages;
    private final Set<SubmoduleId> dependencies;
    
    public SubmoduleDefinition(SubmoduleId id, Set<PackageReference> packages) {
        this.id = id;
        this.packages = packages;
        this.dependencies = Sets.newHashSet();
    }
    
    

}
