package org.tindalos.principle.domain.coredetector;


public interface ViolationsReporter<T extends CheckResult> {
    
    String report(T result);
    
    Class<T> getType();

}
