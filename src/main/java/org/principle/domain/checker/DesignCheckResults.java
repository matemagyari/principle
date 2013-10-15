package org.principle.domain.checker;

import java.util.List;

import org.principle.domain.detector.cycledetector.core.Cycle;
import org.principle.domain.detector.layerviolationdetector.LayerReference;

public class DesignCheckResults {
    
    private final List<LayerReference> violations;
    private final List<Cycle> cycles;
    
    public DesignCheckResults(List<LayerReference> violations, List<Cycle> cycles) {
        this.violations = violations;
        this.cycles = cycles;
    }
    
    public boolean isCyclesDetected() {
        return !cycles.isEmpty();
    }
    public int numOfViolations() {
        return violations.size();
    }

    public boolean hasErrors() {
        return isCyclesDetected() || !violations.isEmpty();
    }
    

    public String getErrorReport() {
        StringBuffer sb = new StringBuffer();
        if (isCyclesDetected()) {
            sb.append("Package cyclesDetected detected!\n");
        }
        if (!violations.isEmpty()) {
            sb.append(errorMessage());
        }
        return sb.toString();
    }
    
    private String errorMessage() {
        StringBuffer sb = new StringBuffer("DDD layers violations: \n");
        for (LayerReference layerReference : violations) {
            sb.append(layerReference + "\n");
        }
        return sb.toString();
    }

    
    

}
