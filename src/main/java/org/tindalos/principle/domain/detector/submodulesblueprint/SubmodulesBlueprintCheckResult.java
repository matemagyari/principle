package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;

import com.google.common.collect.Maps;

public class SubmodulesBlueprintCheckResult implements CheckResult {

	private final Map<Submodule, Set<Submodule>> illegalDependencies;
	private final Map<Submodule, Set<Submodule>> missingDependencies;
	private final Integer threshold;

	public SubmodulesBlueprintCheckResult(SubmodulesBlueprint submodulesBlueprint, Map<Submodule, Set<Submodule>> illegalDependencies,
			Map<Submodule, Set<Submodule>> missingDependencies) {
		this.illegalDependencies = Maps.newHashMap(illegalDependencies);
		this.missingDependencies = Maps.newHashMap(missingDependencies);
		this.threshold = submodulesBlueprint.getViolationsThreshold();
	}

	public boolean expectationsFailed() {
		return violationsNumber() > 0;
	}

	public Map<Submodule, Set<Submodule>> illegalDependencies() {
		return Maps.newHashMap(illegalDependencies);
	}

	public Map<Submodule, Set<Submodule>> missingDependencies() {
		return Maps.newHashMap(missingDependencies);
	}
	
	public int violationsNumber() {
		return illegalDependencies.size() + missingDependencies.size();
	}

	public Integer getThreshold() {
		return threshold;
	}

}
