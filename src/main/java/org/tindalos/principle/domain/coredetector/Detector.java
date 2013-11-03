package org.tindalos.principle.domain.coredetector;

import org.tindalos.principle.domain.expectations.DesignQualityExpectations;


public interface Detector {
    
	CheckResult analyze(CheckInput checkInput);

	boolean isWanted(DesignQualityExpectations designQualityExpectations);

}
