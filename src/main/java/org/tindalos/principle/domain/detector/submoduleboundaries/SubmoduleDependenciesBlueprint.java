package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.List;

import com.google.common.collect.Lists;

public class SubmoduleDependenciesBlueprint {
	
	public List<Submodule> invalidDependencies(Submodule submodule, List<Submodule> dependeeModules) {
		
		List<Submodule> rogueDependencies = Lists.newArrayList(dependeeModules);
		rogueDependencies.removeAll(submodule.getPlannedDependencies());
		return rogueDependencies;
	}

}
