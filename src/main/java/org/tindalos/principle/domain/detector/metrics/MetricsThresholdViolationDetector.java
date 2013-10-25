package org.tindalos.principle.domain.detector.metrics;

import java.util.List;

import org.tindalos.principle.domain.core.Metrics;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.core.Detector;

public class MetricsThresholdViolationDetector implements Detector {

    public CheckResult analyze(CheckInput checkInput) {
        
        List<Package> packages = checkInput.getPackages();
        for (Package aPackage : packages) {
            Metrics metrics = aPackage.getMetrics();
            Metrics metricsThreshold = checkInput.getParameters().getMetricThresholds();
            
        }
        
        return null; 
    }

}
