package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class Submodule {

    private final SubmoduleId id;
    private final Set<SubmoduleId> plannedDependencies;
    private final Set<Package> packagesUnderModule;
    private final Set<PackageReference> outgoingReferences;

    public Submodule(SubmoduleId id, Set<Package> packages, Map<PackageReference, Package> packageReferences) {
        this.id = id;
        this.plannedDependencies = Sets.newHashSet();
        this.packagesUnderModule = Sets.newHashSet(packages);
        this.outgoingReferences = collectOutgoingDependenies(packageReferences);
    }
    
    private Set<PackageReference> collectOutgoingDependenies(Map<PackageReference, Package> packageReferences) {
        Set<PackageReference> allOutgoingReferences = Sets.newHashSet();
        for (Package aPackage : packagesUnderModule) {
            Set<PackageReference> outgoingReferences = aPackage.cumulatedDependencies(packageReferences);
            allOutgoingReferences.addAll(outgoingReferences);
        }
        return allOutgoingReferences;
    }

    public Set<Submodule> findUnAllowedDependencies(List<Submodule> otherSubmodules) {
    	assert !otherSubmodules.contains(this);
        Predicate<Submodule> predicate = new Predicate<Submodule>() {
            public boolean apply(Submodule aSubmodule) {
                return  !plannedDependencies.contains(aSubmodule.getId()) 
                		&& dependsOn(aSubmodule);
            }
        };
        return Sets.newHashSet(Iterables.filter(otherSubmodules, predicate));
    }
    
	private boolean dependsOn(Submodule that) {
		return that.isReferredBy(outgoingReferences);
	}

    private boolean isReferredBy(Set<PackageReference> references) {
        for (PackageReference packageReference : references) {
            for (Package aPackage : packagesUnderModule) {
                if (packageReference.pointsToThatOrInside(aPackage.getReference())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Set<SubmoduleId> getPlannedDependencies() {
        return Sets.newHashSet(plannedDependencies);
    }

	public void setPlannedDependencies(Set<SubmoduleId> plannedDependencies) {
		this.plannedDependencies.addAll(plannedDependencies);
	}
	
    private SubmoduleId getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Submodule)) {
            return false;
        }
        Submodule castOther = (Submodule) other;

        return id.equals(castOther.id);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

	@Override
	public String toString() {
		return id.toString();
	}
    
    

    

}
