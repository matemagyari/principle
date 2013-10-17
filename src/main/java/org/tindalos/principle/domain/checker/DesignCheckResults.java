package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.detector.cycledetector.APDResult;
import org.tindalos.principle.domain.detector.cycledetector.APDViolationsReporter;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerReference;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsReporter;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsResult;
import org.tindalos.principle.domain.reporting.CheckResult;

public class DesignCheckResults {
    
    private final List<LayerReference> layerViolations;
    private final List<Cycle> cycles;
    private final List<CheckResult> checkResults;
    
    public DesignCheckResults(List<CheckResult> checkResults) {
        this.checkResults = checkResults;
        this.layerViolations = ((LayerViolationsResult) checkResults.get(0)).getViolations();
        this.cycles = ((APDResult) checkResults.get(1)).getCycles();
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
        return new APDViolationsReporter().report(cycles);
    }

    private String layerViolationErrorMessage() {
        return new LayerViolationsReporter().report(layerViolations);
    }

    
    

}
