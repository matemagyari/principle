package org.tindalos.principle.domain.checker;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.cycledetector.APDResult;
import org.tindalos.principle.domain.detector.cycledetector.APDViolationsReporter;
import org.tindalos.principle.domain.detector.cycledetector.Cycle;
import org.tindalos.principle.domain.detector.cycledetector.CycleDetector;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerReference;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationDetector;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsReporter;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsResult;

import com.google.common.collect.Maps;

public class DesignCheckResults {
    
    private final Map<String, CheckResult> checkResults;
    
    public DesignCheckResults(List<CheckResult> checkResults) {
    	this.checkResults = Maps.newHashMap();
    	for (CheckResult checkResult : checkResults) {
    		this.checkResults.put(checkResult.detectorId(), checkResult);
		}
    }

    public boolean hasErrors() {
        for (CheckResult checkResult : checkResults.values()) {
            if (checkResult.violationsDetected()) {
                return true;
            }
        }
        return false;
    }
    

    public String getErrorReport() {
        StringBuffer sb = new StringBuffer();
        if (!getCycles().isEmpty()) {
            sb.append(apdViolationErrorMessage());
        }
        if (!getLayerViolations().isEmpty()) {
            sb.append(layerViolationErrorMessage());
        }
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends CheckResult> T getResult(String id) {
        return (T) checkResults.get(id);
    }
    
    private String apdViolationErrorMessage() {
        return new APDViolationsReporter().report(getCycles());
    }

    private List<Cycle> getCycles() {
    	APDResult checkResult = (APDResult) checkResults.get(CycleDetector.ID);
		return checkResult.getCycles();
	}

	private String layerViolationErrorMessage() {
        return new LayerViolationsReporter().report(getLayerViolations());
    }

	private List<LayerReference> getLayerViolations() {
		LayerViolationsResult layerViolationsResult = (LayerViolationsResult) checkResults.get(LayerViolationDetector.ID);
		return layerViolationsResult.getViolations();
	}

}
