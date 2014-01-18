package org.tindalos.principle.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class Package extends PackageScala {

	public Package(String name) {
		super(name);
	}

	public PackageReference getReference() {
		return reference();
	}

    public void insert(Package aPackage) {
        if (this.equals(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert into itself " + this);
        } else if (this.doesNotContain(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this);
        } else if (this.isDirectSuperPackageOf(aPackage)) {
            subPackages().add(aPackage);
        } else {
            insertIndirectSubPackage(aPackage);
        }
    }

	// it dies if there are cycles
	// through references, not through subPackages. transaitive too
	public Set<PackageReference> cumulatedDependencies(Map<PackageReference, Package> packageReferenceMap) {
		return cumulatedDependenciesAcc(packageReferenceMap, new HashSet<PackageReference>());
	}

	private Set<PackageReference> cumulatedDependenciesAcc(Map<PackageReference, Package> packageReferenceMap, Set<PackageReference> dependencies) {

		Set<PackageReference> accumulatedPackageReferences = this.accumulatedDirectPackageReferences();

		accumulatedPackageReferences.removeAll(dependencies);

		if (accumulatedPackageReferences.isEmpty()) {
			dependencies.remove(this.reference());
			return dependencies;
		} else {
			Set<PackageReference> result = Sets.newHashSet(accumulatedPackageReferences);
			for (PackageReference packageReference : accumulatedPackageReferences) {
				Package aPackage = packageReferenceMap.get(packageReference);
				assert aPackage != null : packageReference + " does not exist!";
				dependencies.add(packageReference);
				result.addAll(aPackage.cumulatedDependenciesAcc(packageReferenceMap, dependencies));
				result.remove(this.reference());
			}
			return result;
		}
	}


    private void insertIndirectSubPackage(Package aPackage) {
        String relativeNameOfDirectSubPackage = aPackage.firstPartOfRelativeNameTo(this);
        getSubPackageByRelativeName(relativeNameOfDirectSubPackage).insert(aPackage);
    }
    
	// all the references going out from this package
	public Set<PackageReference> accumulatedDirectPackageReferences() {
		Set<PackageReference> packageReferences = Sets.newHashSet();
		for (Package child : subPackages()) {
			packageReferences.addAll(child.accumulatedDirectPackageReferences());
		}
		packageReferences.addAll(getOwnPackageReferences());
		packageReferences.remove(this.reference());
		return packageReferences;
	}

	private Set<Package> accumulatedDirectlyReferredPackages(Map<PackageReference, Package> packageReferenceMap) {
		Set<PackageReference> packageReferences = accumulatedDirectPackageReferences();
		Set<Package> packages = Sets.newHashSet();
		for (PackageReference reference : packageReferences) {
			packages.add(packageReferenceMap.get(reference));
		}
		assert !packages.contains(this) : "self-references should be excluded";
		return packages;
	}
	
	public CyclesInSubgraph detectCycles(Map<PackageReference, Package> packageReferences) {
		return detectCyclesOnThePathFromHere(TraversedPackages.empty(), CyclesInSubgraph.empty(), packageReferences);
	}

	public Map<PackageReference, Package> toMap() {
		return toMap(new HashMap<PackageReference, Package>());
	}

	private CyclesInSubgraph detectCyclesOnThePathFromHere(
			TraversedPackages traversedPackages, 
			CyclesInSubgraph foundCycles,
			Map<PackageReference, Package> packageReferences) {

	    //enough cycles have been found already with this package
	    if (foundCycles.isBreakingPoint(this)) {
	        return foundCycles;
	    }
	    foundCycles.rememberPackageAsInvestigated(this);
		
		// if we just closed a cycle, add it to the found list then return
		Optional<List<PackageReference>> cycleCandidateEndingHere = findCycleCandidateEndingHere(traversedPackages);
		if (cycleCandidateEndingHere.isPresent()) {
			if (isValid(cycleCandidateEndingHere.get())) {
				foundCycles.add(new Cycle(ListConverter.convert(cycleCandidateEndingHere.get())));
			}
		} else {
			for (Package referencedPackage : this.accumulatedDirectlyReferredPackages(packageReferences)) {

				CyclesInSubgraph cyclesInSubgraph = referencedPackage.detectCyclesOnThePathFromHere(traversedPackages.add(this.getReference()), foundCycles, packageReferences);

				foundCycles.mergeIn(cyclesInSubgraph);
			}
		}
		//System.err.println("Cycles found so far: " + foundCycles.getCycles().size());
		return foundCycles;
	}


	private boolean isValid(List<PackageReference> cycleCandidate) {
		if (cycleCandidate.size() < 2) {
			return false;
		}
		return notEveryNodeUnderFirst(cycleCandidate);
	}

	private Optional<List<PackageReference>> findCycleCandidateEndingHere(TraversedPackages traversedPackages) {

		int indexOfThisPackage = indexInTraversedPath(ListConverter.convert(traversedPackages.packages()));
		if (indexOfThisPackage > -1) {

			List<PackageReference> cycleCandidate = ListConverter.convert(traversedPackages.from(indexOfThisPackage));

			return Optional.of(cycleCandidate);
		} else {
			return Optional.absent();
		}

	}

	private int indexInTraversedPath(List<PackageReference> traversedPackages) {
		int index = traversedPackages.indexOf(this.getReference());
		if (index != -1) {
			// simple case
			return index;
		}

		for (index = 0; index < traversedPackages.size() - 1; index++) {

			PackageReference possibleMatch = traversedPackages.get(index);
			if (possibleMatch.equals(this.getReference())) {
				return index;
			} else if (this.getReference().isDescendantOf(possibleMatch)
			// &&
			// !this.getReference().isDescendantOf(traversedPackages.get(index+1))
					&& notAllAreDescendantsOf(traversedPackages.subList(index + 1, traversedPackages.size()), possibleMatch))

			{
				return index;
			}

		}
		// System.err.println("Failed " + traversedPackages + " " + this);
		return -1;
	}

}
