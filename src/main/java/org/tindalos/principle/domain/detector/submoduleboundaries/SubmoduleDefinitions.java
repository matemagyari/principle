package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SubmoduleDefinitions implements Iterable<SubmoduleId> {
    
    private final Map<SubmoduleId, Set<PackageReference>> submoduleToPackagesMap;

    public SubmoduleDefinitions(Map<SubmoduleId, Set<PackageReference>> submoduleToPackagesMap) {
        this.submoduleToPackagesMap = Maps.newHashMap(submoduleToPackagesMap) ;
    }

    public Iterator<SubmoduleId> iterator() {
        return submoduleToPackagesMap.keySet().iterator();
    }

    public Set<PackageReference> getPackageReferencesFor(SubmoduleId submoduleId) {
        return Sets.newHashSet(submoduleToPackagesMap.get(submoduleId)) ;
    }

}
