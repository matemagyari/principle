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
    
	
	public CyclesInSubgraph detectCycles(Map<PackageReference, Package> packageReferences) {
		return detectCyclesOnThePathFromHere(TraversedPackages.empty(), CyclesInSubgraph.empty(), packageReferences);
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

	private Optional<List<PackageReference>> findCycleCandidateEndingHere(TraversedPackages traversedPackages) {

		int indexOfThisPackage = indexInTraversedPath(ListConverter.convert(traversedPackages.packages()));
		if (indexOfThisPackage > -1) {

			List<PackageReference> cycleCandidate = ListConverter.convert(traversedPackages.from(indexOfThisPackage));

			return Optional.of(cycleCandidate);
		} else {
			return Optional.absent();
		}

	}

}
