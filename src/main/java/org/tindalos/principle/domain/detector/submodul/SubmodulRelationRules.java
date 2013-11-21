package org.tindalos.principle.domain.detector.submodul;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

public class SubmodulRelationRules {
	
	private Map<Submodule, Set<Submodule>> allowedDependenciesMap;

	public List<Submodule> findViolatons(Submodule submodule, List<Submodule> efferentModules) {
		Set<Submodule> allowedDependencies = allowedDependenciesMap.get(submodule);
		
		List<Submodule> rogueDependencies = Lists.newArrayList(efferentModules);
		rogueDependencies.removeAll(allowedDependencies);
		return rogueDependencies;
	}

}
