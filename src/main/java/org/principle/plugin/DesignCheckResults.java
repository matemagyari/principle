package org.principle.plugin;

import java.util.List;

import org.principle.domain.LayerReference;

public class DesignCheckResults {
    
    private final List<LayerReference> violations;
    private final boolean cyclesDetected;
    
    public DesignCheckResults(List<LayerReference> violations, boolean cycles) {
        this.violations = violations;
        this.cyclesDetected = cycles;
    }
    
    public boolean isCyclesDetected() {
        return cyclesDetected;
    }
    public int numOfViolations() {
        return violations.size();
    }

    boolean hasErrors() {
        return cyclesDetected || !violations.isEmpty();
    }
    

    String getErrorReport() {
        StringBuffer sb = new StringBuffer();
        if (cyclesDetected) {
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
