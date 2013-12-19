package org.tindalos.principle.domain.core;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("serial")
public class ReachedCyclesLimitException extends RuntimeException {

    private final Set<Cycle> cycles;
    private final Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints;

    public ReachedCyclesLimitException(Set<Cycle> cycles, Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints) {
        this.cycles = cycles;
        this.cyclesByBreakingPoints = cyclesByBreakingPoints;
    }
    
    public Set<Cycle> getCycles() {
        return Sets.newHashSet(cycles);
    }
    
    public Map<PackageReference, Set<Cycle>> getCyclesByBreakingPoints() {
        return Maps.newHashMap(cyclesByBreakingPoints);
    }

}
