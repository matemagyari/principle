package org.principle.domain.checker;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.detector.cycledetector.CycleDetector;
import org.principle.domain.detector.cycledetector.core.Cycle;
import org.principle.domain.detector.layerviolationdetector.LayerReference;
import org.principle.domain.detector.layerviolationdetector.LayerViolationDetector;

public class DesignChecker {

    private final LayerViolationDetector layerViolationDetector;
    private final CycleDetector cycleDetector;
    
    public DesignChecker(DesingCheckerParameters parameters) {
        layerViolationDetector = new LayerViolationDetector(parameters);
        cycleDetector = new CycleDetector(parameters.getBasePackage());
    }

    @SuppressWarnings("unchecked")
    public DesignCheckResults execute(DesingCheckerParameters parameters) {

        try {
            JDepend jDepend = new JDepend();
            jDepend.addDirectory("./target/classes");
            
            jDepend.addPackage(parameters.getBasePackage());
            Collection<JavaPackage> packages = jDepend.analyze();
            
            List<LayerReference> violations = layerViolationDetector.findViolations(packages);
            List<Cycle> cycles = cycleDetector.analyze(packages);
            
            
            return new DesignCheckResults(violations, cycles);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
