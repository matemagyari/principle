package org.tindalos.principle.domain.checker;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.cycledetector.APDResult;
import org.tindalos.principle.domain.detector.cycledetector.APDViolationsReporter;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsReporter;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsResult;

import com.google.common.collect.Maps;

public class DesignCheckResults {
    
    private final Map<Class<? extends CheckResult>, CheckResult> checkResults;
    
    public DesignCheckResults(List<CheckResult> checkResults) {
    	this.checkResults = Maps.newHashMap();
    	for (CheckResult checkResult : checkResults) {
    		this.checkResults.put(checkResult.getClass(), checkResult);
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
        if (checkResults.get(APDResult.class).violationsDetected()) {
            sb.append(apdViolationErrorMessage());
        }
        if (checkResults.get(LayerViolationsResult.class).violationsDetected()) {
            sb.append(layerViolationErrorMessage());
        }
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends CheckResult> T getResult(Class<T> clazz) {
        return (T) checkResults.get(clazz);
    }
    
    private String apdViolationErrorMessage() {
        APDResult checkResult = (APDResult) checkResults.get(APDResult.class);
        return new APDViolationsReporter().report(checkResult);
    }

	private String layerViolationErrorMessage() {
	    LayerViolationsResult checkResult = (LayerViolationsResult) checkResults.get(LayerViolationsResult.class);
        return new LayerViolationsReporter().report(checkResult);
    }


}
