package org.principle.domain.checker;

import java.util.List;

import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.detector.cycledetector.core.Package;

public interface PackageAnalyzer {
    
    List<Package> analyze(DesingCheckerParameters parameters);

}
