package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
			Set<Submodule> violations = submodule.findUnAllowedDependencies(filter(submodules, submodule));
			if (!violations.isEmpty())
				allViolations.put(submodule, violations);
		}

		return new SubmodulesBlueprintCheckResult(submodulesBlueprint, allViolations);
	}

	private static List<Submodule> filter(List<Submodule> submodules, Submodule submodule) {
		List<Submodule> submodulesCopy = Lists.newArrayList(submodules);
		submodulesCopy.remove(submodule);
		return submodulesCopy;
	}

	public boolean isWanted(DesignQualityExpectations designQualityExpectations) {
		return designQualityExpectations.getSubmodulesBlueprint() != null;
	}

}
