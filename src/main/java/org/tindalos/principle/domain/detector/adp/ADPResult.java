package org.tindalos.principle.domain.detector.adp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.ADP;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ADPResult implements CheckResult {

    private final List<Cycle> cycles;
	private final ADP expectation;
    private final Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints;

    public ADPResult(List<Cycle> cycles, Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints, ADP adpExpectation) {
        this.cyclesByBreakingPoints = Maps.newHashMap(cyclesByBreakingPoints) ;
        this.expectation = adpExpectation;
		this.cycles = Lists.newArrayList(cycles);
		Collections.sort(this.cycles);
    }
    
    public List<Cycle> getCycles() {
        return cycles;
    }

	public boolean expectationsFailed() {
		return cyclesByBreakingPoints.size() > expectation.getViolationsThreshold();
	}

    public Integer getThreshold() {
        return expectation.getViolationsThreshold();
    }
    
    public Map<PackageReference, Set<Cycle>> getCyclesByBreakingPoints() {
        return cyclesByBreakingPoints;
    }

}
