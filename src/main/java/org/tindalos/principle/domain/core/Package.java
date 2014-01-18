package org.tindalos.principle.domain.core;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

public abstract class Package extends PackageScala {

	public Package(String name) {
		super(name);
	}

	public PackageReference getReference() {
		return reference();
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
