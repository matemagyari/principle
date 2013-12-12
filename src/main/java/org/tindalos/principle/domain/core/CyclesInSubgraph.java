package org.tindalos.principle.domain.core;

import java.util.Set;

import com.google.common.collect.Sets;

public class CyclesInSubgraph {
    
    public static int LIMIT = 20;
    
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
        checkCycleLimit();
    }


    public void rememberPackageAsInvestigated(Package aPackage) {
        investigatedPackages.add(aPackage);
    }

    public void mergeIn(CyclesInSubgraph that) {
        this.cycles.addAll(that.cycles);
        this.investigatedPackages.addAll(that.investigatedPackages);
        checkCycleLimit();
    }

    private void checkCycleLimit() {
        if (cycles.size() > LIMIT) {
            throw new ReachedCyclesLimitException(cycles);
        }
    }
    @Override
    public String toString() {
        return "CyclesInSubgraph [cycles=" + cycles + ", investigatedPackages=" + investigatedPackages + "]";
    }
}
