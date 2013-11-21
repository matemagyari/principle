package org.tindalos.principle.domain.detector.submodul;

import java.util.List;
import java.util.Set;

import org.tindalos.principle.domain.core.Package;

import com.google.common.collect.Sets;

public class Submodule {
	
	private final String name;
	private final Set<Package> packages;

	public Submodule(String name, Set<Package> packages) {
		this.name = name;
		this.packages = Sets.newHashSet(packages);
	}

	public List<Submodule> dependsOnAmong(List<Submodule> submodules) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
