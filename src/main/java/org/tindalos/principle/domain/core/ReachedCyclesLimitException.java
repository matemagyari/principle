package org.tindalos.principle.domain.core;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class ReachedCyclesLimitException extends RuntimeException {

    private final Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints;

    public ReachedCyclesLimitException(Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints) {
        this.cyclesByBreakingPoints = cyclesByBreakingPoints;
    }
    
    public Map<PackageReference, Set<Cycle>> getCyclesByBreakingPoints() {
        return Maps.newHashMap(cyclesByBreakingPoints);
    }

}
