package org.principle.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.principle.domain.CycleDetector;
import org.principle.domain.LayerReference;
import org.principle.domain.LayerViolationDetector;
import org.principle.domain.core.Cycle;

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
            
            
            System.err.println("Cycles");
            for (Cycle cycle : cycles) {
                System.err.println(cycle);
            }
            
            System.err.println("violations");
            for (LayerReference violation : violations) {
                System.err.println(violation);
            }
            
            return new DesignCheckResults(violations, jDepend.containsCycles());
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
