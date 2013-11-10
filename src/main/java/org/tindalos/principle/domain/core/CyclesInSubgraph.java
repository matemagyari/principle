package org.tindalos.principle.domain.core;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

public class CyclesInSubgraph {
    
    private final Set<Cycle> cycles;
    private final Set<Package> investigatedPackages;
    
    private CyclesInSubgraph() {
        this.cycles = Sets.newHashSet();
        this.investigatedPackages = Sets.newHashSet();
    }
    
    public static CyclesInSubgraph empty() {
    	return new CyclesInSubgraph();
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

    public void rememberPackageAsInvestigated(Package aPackage) {
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
