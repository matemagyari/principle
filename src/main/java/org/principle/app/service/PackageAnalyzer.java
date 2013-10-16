package org.principle.app.service;

import java.util.List;

import org.principle.domain.core.DesingCheckerParameters;

public interface PackageAnalyzer {
    
    List<org.principle.domain.detector.cycledetector.core.Package> analyze(DesingCheckerParameters parameters);

}
