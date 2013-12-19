package org.tindalos.principle.domain.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CyclesInSubgraph {
    
    private final Set<Package> investigatedPackages;
    private final Map<PackageReference, Set<Cycle>> breakingPoints = Maps.newHashMap();

    private CyclesInSubgraph() {
        this.investigatedPackages = Sets.newHashSet();
    }
    
    public static CyclesInSubgraph empty() {
    	return new CyclesInSubgraph();
    }

    public Set<Package> getInvestigatedPackages() {
        return investigatedPackages;
    }

    public void add(Cycle cycle) {
        PackageReference last =  cycle.getLast();
        Set<Cycle> cyclesForThisBreakingPoint = breakingPoints.get(last);
        if (cyclesForThisBreakingPoint == null) {
            cyclesForThisBreakingPoint = Sets.newHashSet();
            breakingPoints.put(last, cyclesForThisBreakingPoint);
        }
        cyclesForThisBreakingPoint.add(cycle);
    }

    public void rememberPackageAsInvestigated(Package aPackage) {
        investigatedPackages.add(aPackage);
    }

    public void mergeIn(CyclesInSubgraph that) {
        mergeBreakingPoints(that.breakingPoints);
        this.investigatedPackages.addAll(that.investigatedPackages);
    }

    private void mergeBreakingPoints(Map<PackageReference, Set<Cycle>> breakingPointsInOther) {
        for(Entry<PackageReference, Set<Cycle>> entry : breakingPointsInOther.entrySet()) {
            Set<Cycle> cyclesForThisBreakingPoint = this.breakingPoints.get(entry.getKey());
            if (cyclesForThisBreakingPoint == null) {
                this.breakingPoints.put(entry.getKey(), entry.getValue());
            } else {
                cyclesForThisBreakingPoint.addAll(entry.getValue());
            }
        }
    }

    public boolean isBreakingPoint(Package aPackage) {
        Set<Cycle> cyclesForThisBreakingPoint = breakingPoints.get(aPackage.getReference());
        return cyclesForThisBreakingPoint != null && cyclesForThisBreakingPoint.size() > 20;
    }

    public Map<PackageReference, Set<Cycle>> getBreakingPoints() {
        return Maps.newHashMap(breakingPoints);
    }
    
    @Override
    public String toString() {
        return "CyclesInSubgraph [cycles=" + breakingPoints + ", investigatedPackages=" + investigatedPackages + "]";
    }
}
