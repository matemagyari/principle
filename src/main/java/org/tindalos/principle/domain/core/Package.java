package org.tindalos.principle.domain.core;

import java.util.Map;

public abstract class Package extends PackageScala {

	public Package(String name) {
		super(name);
	}

	public CyclesInSubgraph detectCycles(Map<PackageReference, Package> packageReferences) {
		return detectCyclesOnThePathFromHere(TraversedPackages.empty(), CyclesInSubgraph.empty(), packageReferences);
	}


}
