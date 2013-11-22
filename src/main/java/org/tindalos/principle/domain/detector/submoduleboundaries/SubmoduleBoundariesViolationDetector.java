package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;

import com.google.common.collect.Maps;

public class SubmoduleBoundariesViolationDetector implements Detector {
	
	private final SubmodulesFactory submoduleFactory;
	
	public SubmoduleBoundariesViolationDetector(SubmodulesFactory submoduleFactory) {
		this.submoduleFactory = submoduleFactory;
	}

	public CheckResult analyze(CheckInput checkInput) {
	    
	    List<Submodule> submodules = submoduleFactory.buildSubmodules(checkInput.getSubmodulesDefinitionLocation(), checkInput.getPackages(), checkInput.getBasePackage());
	    
		Map<Submodule, Set<Submodule>> allViolations = Maps.newHashMap();
		for (Submodule submodule : submodules) {
			Set<Submodule> violations = submodule.findViolations();
			allViolations.put(submodule, violations);
		}
		
		return new SubmoduleBoundariesCheckResult(allViolations);
	}

	public boolean isWanted(DesignQualityExpectations designQualityExpectations) {
		// TODO Auto-generated method stub
		return false;
	}

}
