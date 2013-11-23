package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;

import com.google.common.collect.Maps;

public class SubmodulesBlueprintCheckResult implements CheckResult {

	private final Map<Submodule, Set<Submodule>> violations;
	private final Integer threshold;

    public SubmodulesBlueprintCheckResult(SubmodulesBlueprint submodulesBlueprint, Map<Submodule, Set<Submodule>> violations) {
        this.violations = Maps.newHashMap(violations);
        this.threshold = submodulesBlueprint.getViolationsThreshold();
    }

    public boolean expectationsFailed() {
		return !violations.isEmpty();
	}
    
    public Map<Submodule, Set<Submodule>> getViolations() {
		return Maps.newHashMap(violations);
	}


    public Integer getThreshold() {
        return threshold;
    }

}
