package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SubmodulesBlueprintViolationDetector implements Detector {

	private final SubmodulesFactory submoduleFactory;

	public SubmodulesBlueprintViolationDetector(SubmodulesFactory submoduleFactory) {
		this.submoduleFactory = submoduleFactory;
	}

	public CheckResult analyze(CheckInput checkInput) {

		SubmodulesBlueprint submodulesBlueprint = checkInput.getSubmodulesBlueprint();
		
		List<Submodule> submodules = submoduleFactory.buildSubmodules(submodulesBlueprint.getSubmodulesDefinitionLocation(), checkInput.getPackages(),
				checkInput.getBasePackage());

		Map<Submodule, Set<Submodule>> allViolations = Maps.newHashMap();
		for (Submodule submodule : submodules) {
			Set<Submodule> violations = submodule.findIllegalDependencies(filter(submodules, submodule));
			if (!violations.isEmpty())
				allViolations.put(submodule, violations);
		}

		return new SubmodulesBlueprintCheckResult(submodulesBlueprint, allViolations);
	}

	private static Set<Submodule> filter(List<Submodule> submodules, Submodule submodule) {
		Set<Submodule> submodulesCopy = Sets.newHashSet(submodules);
		submodulesCopy.remove(submodule);
		return submodulesCopy;
	}

	public boolean isWanted(DesignQualityExpectations designQualityExpectations) {
		return designQualityExpectations.getSubmodulesBlueprint() != null;
	}

}
