package org.tindalos.principle.domain.reporting;

import org.tindalos.principle.domain.detector.core.CheckResult;

public interface ViolationsReporter<T extends CheckResult> {
    
    String report(T result);
    
    Class<T> getType();

}
