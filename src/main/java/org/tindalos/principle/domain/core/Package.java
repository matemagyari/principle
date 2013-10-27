package org.tindalos.principle.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class Package {

    private final List<Package> children = Lists.newArrayList();
    private final PackageReference reference;

    public Package(String name) {
        this.reference = new PackageReference(name);
    }

    public PackageReference getReference() {
        return reference;
    }

    public void insert(Package aPackage) {
        if (this.equals(aPackage)) {
            throw new RuntimeException("Attempted to insert into itself " + this);
        }
        if (this.isNotAnAncestorOf(aPackage)) {
            throw new RuntimeException(aPackage + " is not under " + this);
        }
        if (this.isDirectParentOf(aPackage)) {
            children.add(aPackage);
        } else {
            insertIndirectChild(aPackage);
        }

    }
    
    public int calculateNumberOfCumulatedDependees() {
    	Set<PackageReference> accumulatedPackageReferences = this.accumulatedPackageReferences();
    	final PackageReference reference = this.reference;
    	Predicate<PackageReference> filter = new Predicate<PackageReference>() {
			public boolean apply(PackageReference input) {
				return reference.isNotAnAncestorOf(input);
			}
		};
		return Sets.newHashSet(Iterables.filter(accumulatedPackageReferences, filter)).size() + 1;
    }
    
    public CCDValue calculateCCD(Map<PackageReference, Package> packageReferenceMap) {
    	
    	Set<PackageReference> accumulatedPackageReferences = accumulatedPackageReferences();
		
    	int componentDependency = accumulatedPackageReferences.size() + 1;
    	int cumulatedComponentDependency = componentDependency;
		
		for (PackageReference packageReference : accumulatedPackageReferences) {
			Package dependee = packageReferenceMap.get(packageReference);
			CCDValue dependeeCCD = dependee.calculateCCD(packageReferenceMap);
			cumulatedComponentDependency += dependeeCCD.getCumulatedComponentDependency();
		}
		CCDValue result = new CCDValue(componentDependency, cumulatedComponentDependency);
		System.err.println(this + "  " + result);
		return result;
    }

    public List<Cycle> detectCycles(Map<PackageReference, Package> packageReferences) {
        Set<Cycle> cycles = detectCycles(new ArrayList<PackageReference>(), new HashSet<Cycle>(), packageReferences);
        return Lists.newArrayList(cycles);
    }
    
    public Map<PackageReference, Package> toMap() {
        return toMap(new HashMap<PackageReference, Package>());
    }
    
    private boolean isDirectParentOf(Package aPackage) {
        return this.getReference().isDirectParentOf(aPackage.getReference());
    }

    private boolean isNotAnAncestorOf(Package aPackage) {
        return this.getReference().isNotAnAncestorOf(aPackage.getReference());
    }

    private void insertIndirectChild(Package aPackage) {
        String relativeNameOfDirectChild = aPackage.firstPartOfRelativeNameTo(this);
        Package directChild = getChild(relativeNameOfDirectChild);
        if (directChild == null) {
            directChild = createNew(this.getReference().toString() + "." + relativeNameOfDirectChild);
            children.add(directChild);
        }
        directChild.insert(aPackage);
    }

    protected Package createNew(String name) {
        return new Package(name) {
            @Override
            public Set<PackageReference> getOwnPackageReferences() {
                return Sets.newHashSet();
            }

            @Override
            public Metrics getMetrics() {
                return Metrics.undefined();
            }
            
        };
    }

    private String firstPartOfRelativeNameTo(Package parentPackage) {
        return this.getReference().firstPartOfRelativeNameTo(parentPackage.getReference());
    }

    private Set<Cycle> detectCycles(List<PackageReference> traversedPackages, Set<Cycle> foundCycles, Map<PackageReference, Package> packageReferences) {

        //if we just closed a cycle, add it to the found list then return
        int indexOfThisPackage = traversedPackages.indexOf(this.getReference());
        if (indexOfThisPackage > -1) {
            Cycle cycleEndingWithThisPackage = new Cycle(traversedPackages.subList(indexOfThisPackage, traversedPackages.size()));
            if (cycleEndingWithThisPackage.notSingleNode()) {
                foundCycles.add(cycleEndingWithThisPackage);
            }
            return foundCycles;
        }
        //otherwise loop through accumulated references
        for (PackageReference referencedPackageRef : this.accumulatedPackageReferences()) {

            List<PackageReference> updatedTraversedPackages = Lists.newArrayList(traversedPackages);
            updatedTraversedPackages.add(this.getReference());

            Package referencedPackage = packageReferences.get(referencedPackageRef);
            Set<Cycle> cycles = referencedPackage.detectCycles(updatedTraversedPackages, foundCycles, packageReferences);
            foundCycles.addAll(cycles);
        }
        return foundCycles;
    }

    public Set<PackageReference> accumulatedPackageReferences() {
        Set<PackageReference> packageReferences = Sets.newHashSet();
        for (Package child : children) {
            packageReferences.addAll(child.accumulatedPackageReferences());
        }
        packageReferences.addAll(getOwnPackageReferences());

        return packageReferences;
    }

    private Package getChild(String relativeName) {
        for (Package child : children) {
            if (child.getReference().equals(this.getReference().child(relativeName))) {
                return child;
            }
        }
        return null;
    }

    private Map<PackageReference, Package> toMap(Map<PackageReference, Package> accumulatingMap) {

        accumulatingMap.put(this.getReference(), this);
        for (Package child : this.children) {
            child.toMap(accumulatingMap);
        }
        return accumulatingMap;
    }
    
    public Float instability() {
    	return getMetrics().getInstability();
    }

	public Float distance() {
		return getMetrics().getDistance();
	}
	
    public abstract Set<PackageReference> getOwnPackageReferences();
    public abstract Metrics getMetrics();

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


}
