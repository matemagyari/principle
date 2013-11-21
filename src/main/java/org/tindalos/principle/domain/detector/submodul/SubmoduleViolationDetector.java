package org.tindalos.principle.domain.detector.submodul;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;

import com.google.common.collect.Maps;

public class SubmoduleViolationDetector implements Detector {
	
	private final SubmoduleFactory submoduleFactory;
	
	public SubmoduleViolationDetector(SubmoduleFactory submoduleFactory) {
		this.submoduleFactory = submoduleFactory;
	}

	public CheckResult analyze(CheckInput checkInput) {
		
		List<Submodule> submodules = submoduleFactory.buildModules(checkInput.getPackages());
		SubmodulRelationRules submodulRelationRules = new SubmodulRelationRules();
		Map<Submodule, List<Submodule>> allViolations = Maps.newHashMap();
		for (Submodule submodule : submodules) {
			List<Submodule> efferentModules = submodule.dependsOnAmong(submodules);
			List<Submodule> violations = submodulRelationRules.findViolatons(submodule, efferentModules);
			allViolations.put(submodule, violations);
		}
		
		return new ModuleDependenciesResult();
	}

	public boolean isWanted(DesignQualityExpectations designQualityExpectations) {
		// TODO Auto-generated method stub
		return false;
	}

}
