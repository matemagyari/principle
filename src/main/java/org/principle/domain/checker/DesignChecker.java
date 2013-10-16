package org.principle.domain.checker;

import java.util.List;

import org.principle.domain.core.Cycle;
import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.core.Package;
import org.principle.domain.detector.cycledetector.CycleDetector;
import org.principle.domain.detector.layerviolationdetector.LayerReference;
import org.principle.domain.detector.layerviolationdetector.LayerViolationDetector;

public class DesignChecker {

    private final LayerViolationDetector layerViolationDetector;
    private final CycleDetector cycleDetector;

    public DesignChecker(LayerViolationDetector layerViolationDetector, CycleDetector cycleDetector) {
        this.layerViolationDetector = layerViolationDetector;
        this.cycleDetector = cycleDetector;

    }

    public DesignCheckResults execute(List<Package> packages, DesingCheckerParameters parameters) {

        List<LayerReference> violations = layerViolationDetector.findViolations(packages, parameters);
        List<Cycle> cycles = cycleDetector.analyze(packages, parameters);

        return new DesignCheckResults(violations, cycles);
    }

}
