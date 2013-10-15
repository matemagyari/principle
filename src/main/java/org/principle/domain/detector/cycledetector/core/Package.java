package org.principle.domain.detector.cycledetector.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Package {

    private final List<Package> children = Lists.newArrayList();
    private final Set<PackageReference> ownPackageReferences;
    private final PackageReference reference;

    public Package(String name, Set<PackageReference> packageReferences) {
        this.reference = new PackageReference(name);
        this.ownPackageReferences = Sets.newHashSet((packageReferences));
    }

    public Package(String name) {
        this(name, new HashSet<PackageReference>());
    }

    protected PackageReference getReference() {
        return reference;
    }
    
    private Package find(PackageReference reference) {
        if (this.getReference().equals(reference)) {
            return this;
        }
        if (notUnderMe(reference)) {
            return null;
        }
        for (Package child : children) {
            Package foundPackage = child.find(reference);
            if (foundPackage != null) {
                return foundPackage;
            }
        }
        return null;
    }
    
    public List<Package> flatten() {
        List<Package> flattenedHierarchy = Lists.newArrayList();
        flattenedHierarchy = Lists.newArrayList(this);

        for (Package child : children) {
            flattenedHierarchy.addAll(child.flatten());

        }
        return flattenedHierarchy;
    }


    public void insert(Package aPackage) {
        if (this.equals(aPackage)) {
            throw new RuntimeException("Attempted to insert into itself " + this);
        }
        if (notUnderMe(aPackage)) {
            throw new RuntimeException(aPackage + " is not under " + this);
        }
        if (isDirectChildOfMe(aPackage)) {
            children.add(aPackage);
        } else {
            insertIndirectChild(aPackage);
        }

    }
    
    public Set<Cycle> detectCyclesBelow() {

        Set<Cycle> cycles = Sets.newHashSet();

        for (Package child : children) {
            cycles.addAll(child.detectCyclesBelow());

            if (child.explicitlyRefersTo(this) && this.explicitlyRefersTo(child)) {
                cycles.add(new Cycle(this.getReference(), child.getReference()));
            }
        }

        cycles.addAll(detectCyclesBetweenChildren());
        return cycles;
    }
    
    public List<Cycle> detectCycles(Map<PackageReference, Package> packageReferences) {
        Set<Cycle> cycles = detectCycles(new ArrayList<PackageReference>(), new HashSet<Cycle>(), packageReferences);
        return Lists.newArrayList(cycles);
    }
    
    public Map<PackageReference, Package> toMap() {
        return toMap(new HashMap<PackageReference, Package>());
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
        return new Package(name);
    }

    private String firstPartOfRelativeNameTo(Package parentPackage) {
        return this.getReference().firstPartOfRelativeNameTo(parentPackage.getReference());
    }

    private boolean isDirectChildOfMe(Package aPackage) {
        return this.getReference().isDirectChildOfMe(aPackage.getReference());
    }

    private boolean notUnderMe(Package aPackage) {
        return notUnderMe(aPackage.getReference());
    }

    private boolean notUnderMe(PackageReference reference) {
        return this.getReference().notUnderMe(reference);
    }

    private Set<Cycle> detectCyclesBetweenChildren() {
        Set<Cycle> cyclesAmongChildren = Sets.newHashSet();
        for (Package child : children) {

            List<Cycle> cycles = child
                    .detectCycles(children, new ArrayList<PackageReference>(), new ArrayList<Cycle>());
            cyclesAmongChildren.addAll(cycles);
        }
        return cyclesAmongChildren;
    }

    private List<Cycle> detectCycles(List<Package> relevantPackages, List<PackageReference> traversedPackages,
            List<Cycle> foundCycles) {

        //if we just closed a cycle, add it to the found list then return
        int indexOfThisPackage = traversedPackages.indexOf(this.getReference());
        if (indexOfThisPackage > -1) {
            Cycle cycleEndingWithThisPackage = new Cycle(traversedPackages.subList(indexOfThisPackage, traversedPackages.size()));
            if (cycleEndingWithThisPackage.notSingleNode()) {
                foundCycles.add(cycleEndingWithThisPackage);
            }
            return foundCycles;
        }
        //otherwise
        for (Package relevantPackage : relevantPackages) {
            if (this.refersTo(relevantPackage)) {

                List<PackageReference> updatedTraversedPackages = Lists.newArrayList(traversedPackages);
                updatedTraversedPackages.add(this.getReference());

                List<Cycle> cycles = relevantPackage.detectCycles(relevantPackages, updatedTraversedPackages,
                        foundCycles);
                foundCycles.addAll(cycles);
            }
        }

        return foundCycles;
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

    private boolean refersTo(Package aPackage) {
        if (this.equals(aPackage)) {
            return false;
        }

        for (PackageReference reference : this.accumulatedPackageReferences()) {
            if (aPackage.pointsInside(reference)) {
                return true;
            }
        }
        return false;
    }

    private boolean pointsInside(PackageReference reference) {
        return reference.pointsInside(this.getReference());
    }

    private boolean pointsToMe(PackageReference reference) {
        return reference.equals(this.getReference());
    }

    private boolean explicitlyRefersTo(Package aPackage) {
        for (PackageReference reference : this.getOwnPackageReferences()) {
            if (aPackage.pointsToMe(reference)) {
                return true;
            }
        }
        return false;
    }

    private Set<PackageReference> accumulatedPackageReferences() {
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

    protected Set<PackageReference> getOwnPackageReferences() {
        return ownPackageReferences;
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

}
