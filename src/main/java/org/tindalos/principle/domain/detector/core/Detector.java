package org.tindalos.principle.domain.detector.core;


public interface Detector {
    
    CheckResult analyze(CheckInput checkInput);

}
