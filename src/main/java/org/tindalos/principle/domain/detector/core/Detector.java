package org.tindalos.principle.domain.detector.core;


public interface Detector<T extends CheckResult> {
    
    T analyze(CheckInput checkInput);

}
