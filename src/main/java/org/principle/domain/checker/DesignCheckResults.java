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
    
    public int numOfLayerViolations() {
        return violations.size();
    }
    public int numOfADPViolations() {
        return cycles.size();
    }

    public boolean hasErrors() {
        return !cycles.isEmpty() || !violations.isEmpty();
    }
    

    public String getErrorReport() {
        StringBuffer sb = new StringBuffer();
        if (!cycles.isEmpty()) {
            sb.append(apdViolationErrorMessage());
        }
        if (!violations.isEmpty()) {
            sb.append(layerViolationErrorMessage());
        }
        return sb.toString();
    }
    
    private String apdViolationErrorMessage() {
        StringBuffer sb = new StringBuffer("APD violations: \n");
        for (Cycle cycle : cycles) {
            sb.append(cycle + "\n");
        }
        return sb.toString();
    }

    private String layerViolationErrorMessage() {
        StringBuffer sb = new StringBuffer("DDD layers violations: \n");
        for (LayerReference layerReference : violations) {
            sb.append(layerReference + "\n");
        }
        return sb.toString();
    }

    
    

}
