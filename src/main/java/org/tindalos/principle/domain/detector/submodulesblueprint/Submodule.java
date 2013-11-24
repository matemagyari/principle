package org.tindalos.principle.domain.detector.submodulesblueprint;

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

    public Submodule(SubmoduleId id, Set<Package> packagesUnderModule) {
        this.id = id;
        this.plannedDependencies = Sets.newHashSet();
        this.packagesUnderModule = Sets.newHashSet(packagesUnderModule);
        this.outgoingReferences = collectOutgoingDependenies();
    }
    
    private Set<PackageReference> collectOutgoingDependenies() {
        Set<PackageReference> allOutgoingReferences = Sets.newHashSet();
        for (Package aPackage : packagesUnderModule) {
            Set<PackageReference> outgoingReferences = aPackage.accumulatedDirectPackageReferences();
            allOutgoingReferences.addAll(outgoingReferences);
        }
        return allOutgoingReferences;
    }
    
	public Set<Submodule> findMissingDependencies(Set<Submodule> otherSubmodules) {
		assert !otherSubmodules.contains(this);
		
		Set<Submodule> legalDependencies = filterLegalDependencies(otherSubmodules);
		
        Predicate<Submodule> missingDependenciesPredicate = new Predicate<Submodule>() {
            public boolean apply(Submodule aSubmodule) {
                return !dependsOnBy(aSubmodule, outgoingReferences);
            }
        };
        return Sets.newHashSet(Iterables.filter(legalDependencies, missingDependenciesPredicate));
	}

    public Set<Submodule> findIllegalDependencies(Set<Submodule> otherSubmodules) {
    	assert !otherSubmodules.contains(this);
    	
    	Set<Submodule> legalDependencies = filterLegalDependencies(otherSubmodules);
    	
    	otherSubmodules.removeAll(legalDependencies);
    	
    	if (otherSubmodules.isEmpty()) {
    		return Sets.newHashSet();
    	}
    	
    	final Set<PackageReference> extraReferences = extraReferences(legalDependencies);
    	
        Predicate<Submodule> illegalDependenciesPredicate = new Predicate<Submodule>() {
            public boolean apply(Submodule aSubmodule) {
                return dependsOnBy(aSubmodule, extraReferences);
            }
        };
        return Sets.newHashSet(Iterables.filter(otherSubmodules, illegalDependenciesPredicate));
    }

	private Set<Submodule> filterLegalDependencies(Set<Submodule> others) {
        Predicate<Submodule> predicate = new Predicate<Submodule>() {
            public boolean apply(Submodule other) {
                return plannedDependencies.contains(other.getId());
            }
        };
        return Sets.newHashSet(Iterables.filter(others, predicate));
	}

	private Set<PackageReference> extraReferences(Set<Submodule> legalDependencies) {
		final Set<PackageReference> potentiallyIllegalReferences = Sets.newHashSet(outgoingReferences);
    	for (Submodule legalDependency : legalDependencies) {
    		legalDependency.removeOutsideReferences(potentiallyIllegalReferences);
		}
		return potentiallyIllegalReferences;
	}
    
	private boolean dependsOn(Submodule that) {
		return that.isReferredBy(outgoingReferences);
	}

	private boolean dependsOnBy(Submodule that, Set<PackageReference> references) {
		return that.isReferredBy(references);
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

    private void removeOutsideReferences(Set<PackageReference> references) {
    	Set<PackageReference> referencesCopy = Sets.newHashSet(references);
        for (PackageReference packageReference : referencesCopy) {
            for (Package aPackage : packagesUnderModule) {
                if (packageReference.pointsToThatOrInside(aPackage.getReference())) {
                	references.remove(packageReference);
                }
            }
        }
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
