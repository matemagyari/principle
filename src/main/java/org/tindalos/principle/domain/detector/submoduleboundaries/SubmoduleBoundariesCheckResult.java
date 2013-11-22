package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.CheckResult;

import com.google.common.collect.Maps;

public class SubmoduleBoundariesCheckResult implements CheckResult {

	private final Map<Submodule, Set<Submodule>> allViolations;

    public SubmoduleBoundariesCheckResult(Map<Submodule, Set<Submodule>> allViolations) {
        this.allViolations = Maps.newHashMap(allViolations);
    }

    public boolean expectationsFailed() {
		return !allViolations.isEmpty();
	}

}
