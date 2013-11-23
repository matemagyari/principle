package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Collection;
import java.util.Set;

import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Sets;

public class SubmoduleDefinition {
    
    private final SubmoduleId id;
    private final Set<PackageReference> packages;
    private final Set<SubmoduleId> plannedDependencies;
    
    public SubmoduleDefinition(SubmoduleId id, Set<PackageReference> packages) {
        this.id = id;
        this.packages = Sets.newHashSet(packages);
        this.plannedDependencies = Sets.newHashSet();
    }
    
    public SubmoduleId getId() {
		return id;
	}
    
    public Set<PackageReference> getPackages() {
		return Sets.newHashSet(packages);
	}
    
    public Set<SubmoduleId> getPlannedDependencies() {
		return Sets.newHashSet(plannedDependencies);
	}
    
    public void addPlannedDependencies(Collection<SubmoduleId> plannedDependencies) {
    	this.plannedDependencies.addAll(plannedDependencies);
    }

	@Override
	public String toString() {
		return "SubmoduleDefinition [" + id + "]";
	}
    
}
