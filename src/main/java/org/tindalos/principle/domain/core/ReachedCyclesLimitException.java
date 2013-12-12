package org.tindalos.principle.domain.core;

import java.util.Set;

import com.google.common.collect.Sets;

@SuppressWarnings("serial")
public class ReachedCyclesLimitException extends RuntimeException {

    private final Set<Cycle> cycles;

    public ReachedCyclesLimitException(Set<Cycle> cycles) {
        this.cycles = cycles;
    }
    
    public Set<Cycle> getCycles() {
        return Sets.newHashSet(cycles);
    }

}
