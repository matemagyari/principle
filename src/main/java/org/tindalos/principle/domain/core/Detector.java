package org.tindalos.principle.domain.core;

import org.tindalos.principle.domain.reporting.CheckResult;

public interface Detector {
    
    CheckResult analyze(CheckInput checkInput);

}
