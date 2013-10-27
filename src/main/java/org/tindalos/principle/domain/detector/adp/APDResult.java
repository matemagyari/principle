package org.tindalos.principle.domain.detector.adp;

import java.util.List;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.coredetector.CheckResult;

import com.google.common.collect.Lists;

public class APDResult implements CheckResult {

    private final List<Cycle> cycles;

    public APDResult(List<Cycle> cycles) {
        this.cycles = Lists.newArrayList(cycles);
    }
    
    public List<Cycle> getCycles() {
        return cycles;
    }

	public String detectorId() {
		return CycleDetector.ID;
	}

    public boolean violationsDetected() {
        return !cycles.isEmpty();
    }

    public int numberOfViolations() {
        return cycles.size();
    }

}
