package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Maps;

public class SubmoduleDefinitions implements Iterable<SubmoduleDefinition> {
    
    private final Map<SubmoduleId, SubmoduleDefinition> definitions;

    public SubmoduleDefinitions(Map<SubmoduleId, SubmoduleDefinition> definitions) {
		this.definitions = Maps.newHashMap(definitions);
	}

	public SubmoduleDefinitions(List<SubmoduleDefinition> theDefinitions) {
        this.definitions = Maps.newHashMap();
        for (SubmoduleDefinition submoduleDefinition : theDefinitions) {
        	definitions.put(submoduleDefinition.getId(), submoduleDefinition);
		}
    }

    public Iterator<SubmoduleDefinition> iterator() {
        return definitions.values().iterator();
    }
    
    public Set<PackageReference> getPackages(SubmoduleId submoduleId) {
    	return definitions.get(submoduleId).getPackages();
    }

}
