package org.tindalos.principle.domain.core;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

public class CyclesInSubgraph {
    
    private final Set<Cycle> cycles;
    private final Set<Package> investigatedPackages;
    
    public CyclesInSubgraph(Set<Cycle> cycles, Set<Package> investigatedPackages) {
        this.cycles = Sets.newHashSet(cycles);
        this.investigatedPackages = Sets.newHashSet(investigatedPackages);
    }

    public CyclesInSubgraph() {
        this(new HashSet<Cycle>(), new HashSet<Package>());
    }
    
    public Set<Cycle> getCycles() {
        return cycles;
    }
    
    public Set<Package> getInvestigatedPackages() {
        return investigatedPackages;
    }

    public void add(Cycle cycle) {
        cycles.add(cycle);
    }

    public void add(Package aPackage) {
        investigatedPackages.add(aPackage);
    }

    public void mergeIn(CyclesInSubgraph that) {
        this.cycles.addAll(that.cycles);
        this.investigatedPackages.addAll(that.investigatedPackages);
    }

    @Override
    public String toString() {
        return "CyclesInSubgraph [cycles=" + cycles + ", investigatedPackages=" + investigatedPackages + "]";
    }
}
