package org.principle.domain.checker;

import java.util.List;

import org.principle.domain.core.Cycle;
import org.principle.domain.detector.layerviolationdetector.LayerReference;

public class DesignCheckResults {
    
    private final List<LayerReference> layerViolations;
    private final List<Cycle> cycles;
    
    public DesignCheckResults(List<LayerReference> violations, List<Cycle> cycles) {
        this.layerViolations = violations;
        this.cycles = cycles;
    }
    
    public int numOfLayerViolations() {
        return layerViolations.size();
    }
    public int numOfADPViolations() {
        return cycles.size();
    }

    public boolean hasErrors() {
        return !cycles.isEmpty() || !layerViolations.isEmpty();
    }
    

    public String getErrorReport() {
        StringBuffer sb = new StringBuffer();
        if (!cycles.isEmpty()) {
            sb.append(apdViolationErrorMessage());
        }
        if (!layerViolations.isEmpty()) {
            sb.append(layerViolationErrorMessage());
        }
        return sb.toString();
    }
    
    private String apdViolationErrorMessage() {
        StringBuffer sb = new StringBuffer("APD layerViolations: \n");
        for (Cycle cycle : cycles) {
            sb.append(cycle + "\n");
        }
        return sb.toString();
    }

    private String layerViolationErrorMessage() {
        StringBuffer sb = new StringBuffer("DDD layers layerViolations: \n");
        for (LayerReference layerReference : layerViolations) {
            sb.append(layerReference + "\n");
        }
        return sb.toString();
    }

    
    

}
