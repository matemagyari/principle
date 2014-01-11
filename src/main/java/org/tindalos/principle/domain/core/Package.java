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

public abstract class Package {

	private final List<Package> subPackages = Lists.newArrayList();
	private final PackageReference reference;

	public Package(String name) {
		this.reference = new PackageReference(name);
	}

	public PackageReference getReference() {
		return reference;
	}

    public void insert(Package aPackage) {
        if (this.equals(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert into itself " + this);
        } else if (this.doesNotContain(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this);
        } else if (this.isDirectSuperPackageOf(aPackage)) {
            subPackages.add(aPackage);
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
			dependencies.remove(this.reference);
			return dependencies;
		} else {
			Set<PackageReference> result = Sets.newHashSet(accumulatedPackageReferences);
			for (PackageReference packageReference : accumulatedPackageReferences) {
				Package aPackage = packageReferenceMap.get(packageReference);
				assert aPackage != null : packageReference + " does not exist!";
				dependencies.add(packageReference);
				result.addAll(aPackage.cumulatedDependenciesAcc(packageReferenceMap, dependencies));
				result.remove(this.reference);
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
		for (Package child : subPackages) {
			packageReferences.addAll(child.accumulatedDirectPackageReferences());
		}
		packageReferences.addAll(getOwnPackageReferences());
		packageReferences.remove(this.reference);
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

	private boolean isDirectSuperPackageOf(Package aPackage) {
		return this.getReference().isDirectParentOf(aPackage.getReference());
	}

	private boolean doesNotContain(Package aPackage) {
		return !aPackage.getReference().pointsInside(this.getReference());
	}


	protected Package createNew(String name) {
		return new Package(name) {
			@Override
			public Set<PackageReference> getOwnPackageReferences() {
				return Sets.newHashSet();
			}

			@Override
			public Metrics getMetrics() {
				return UndefinedMetrics$.MODULE$;
			}

			@Override
			public boolean isUnreferred() {
				return true;
			}

			@Override
			public boolean isIsolated() {
				return true;
			}

		};
	}

	private String firstPartOfRelativeNameTo(Package parentPackage) {
		return this.getReference().firstPartOfRelativeNameTo(parentPackage.getReference());
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
				foundCycles.add(new Cycle(cycleCandidateEndingHere.get()));
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

	private boolean notEveryNodeUnderFirst(List<PackageReference> cycleCandidate) {
		PackageReference first = cycleCandidate.get(0);
		for (int i = 1; i < cycleCandidate.size(); i++) {
			if (!cycleCandidate.get(i).isDescendantOf(first)) {
				return true;
			}
		}
		return first.equals(this.getReference());
	}

	private boolean isValid(List<PackageReference> cycleCandidate) {
		if (cycleCandidate.size() < 2) {
			return false;
		}
		return notEveryNodeUnderFirst(cycleCandidate);
	}

	private Optional<List<PackageReference>> findCycleCandidateEndingHere(TraversedPackages traversedPackages) {

		int indexOfThisPackage = indexInTraversedPath(traversedPackages.toList());
		if (indexOfThisPackage > -1) {

			List<PackageReference> cycleCandidate = traversedPackages.from(indexOfThisPackage);

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

	private boolean notAllAreDescendantsOf(List<PackageReference> packages, PackageReference possibleAncestor) {
		for (PackageReference packageReference : packages) {
			if (!packageReference.isDescendantOf(possibleAncestor)) {
				return true;
			}
		}
		return false;
	}

	private Package getSubPackageByRelativeName(String relativeName) {
		for (Package subPackage : subPackages) {
			if (subPackage.getReference().equals(this.getReference().child(relativeName))) {
				return subPackage;
			}
		}
		Package directSubPackage = createNew(this.getReference().createChild(relativeName));
		subPackages.add(directSubPackage);
		return directSubPackage;
	}

	private Map<PackageReference, Package> toMap(Map<PackageReference, Package> accumulatingMap) {

		accumulatingMap.put(this.getReference(), this);
		for (Package child : this.subPackages) {
			child.toMap(accumulatingMap);
		}
		return accumulatingMap;
	}

	public Float instability() {
		return getMetrics().instability();
	}

	public Float distance() {
		return getMetrics().distance();
	}

	public abstract Set<PackageReference> getOwnPackageReferences();

	public abstract Metrics getMetrics();

	public abstract boolean isUnreferred();

	public boolean isIsolated() {
		return getMetrics().afferentCoupling() == 0 && getMetrics().efferentCoupling() == 0;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Package)) {
			return false;
		}
		Package castOther = (Package) other;
		return new EqualsBuilder().append(reference, castOther.reference).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(reference).hashCode();
	}

	@Override
	public String toString() {
		return this.reference.toString();
	}
	
	private static class TraversedPackages {
	    private List<PackageReference> packages;
	    static TraversedPackages empty() {
	        return new TraversedPackages(new ArrayList<PackageReference>());
	    }
        List<PackageReference> toList() {
            return Lists.newArrayList(packages);
        }
        TraversedPackages add(PackageReference reference) {
            List<PackageReference> copy = toList();
            copy.add(reference);
            return new TraversedPackages(copy);
        }
        List<PackageReference> from(int index) {
            return packages.subList(index, packages.size());
        }
        private TraversedPackages(List<PackageReference> sofar) {
            this.packages = Lists.newArrayList(sofar);
        }
	    
	}


}
