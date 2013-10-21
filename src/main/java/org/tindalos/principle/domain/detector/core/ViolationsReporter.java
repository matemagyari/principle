package org.tindalos.principle.domain.detector.core;


public interface ViolationsReporter<T extends CheckResult> {
    
    String report(T result);
    
    Class<T> getType();

}
