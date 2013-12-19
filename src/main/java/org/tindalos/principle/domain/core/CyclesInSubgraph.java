package org.tindalos.principle.domain.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CyclesInSubgraph {
    
    private static final int LIMIT = 25;
    private final Set<Package> investigatedPackages = Sets.newHashSet();
    private final Map<PackageReference, Set<Cycle>> breakingPoints = Maps.newHashMap();

    public static CyclesInSubgraph empty() {
    	return new CyclesInSubgraph();
    }

    public Set<Package> investigatedPackages() {
        return Sets.newHashSet(investigatedPackages);
    }
    
    public Map<PackageReference, Set<Cycle>> cycles() {
        return Maps.newHashMap(breakingPoints);
    }

    void add(Cycle cycle) {
        PackageReference last =  cycle.getLast();
        Set<Cycle> cyclesForThisBreakingPoint = breakingPoints.get(last);
        if (cyclesForThisBreakingPoint == null) {
            cyclesForThisBreakingPoint = Sets.newHashSet();
            breakingPoints.put(last, cyclesForThisBreakingPoint);
        }
        cyclesForThisBreakingPoint.add(cycle);
    }

    void rememberPackageAsInvestigated(Package aPackage) {
        investigatedPackages.add(aPackage);
    }

    void mergeIn(CyclesInSubgraph that) {
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

    boolean isBreakingPoint(Package aPackage) {
        Set<Cycle> cyclesForThisBreakingPoint = breakingPoints.get(aPackage.getReference());
        return cyclesForThisBreakingPoint != null && cyclesForThisBreakingPoint.size() > LIMIT;
    }


    
    @Override
    public String toString() {
        return "CyclesInSubgraph [cycles=" + breakingPoints + ", investigatedPackages=" + investigatedPackages + "]";
    }
}
