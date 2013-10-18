package org.tindalos.principle.domain.detector.layerviolationdetector;

import java.util.List;

import org.tindalos.principle.domain.detector.core.CheckResult;

import com.google.common.collect.Lists;

public class LayerViolationsResult implements CheckResult {

    private final List<LayerReference> references;

    public LayerViolationsResult(List<LayerReference> references) {
        this.references = Lists.newArrayList(references);
    }
    
    public List<LayerReference> getViolations() {
        return references;
    }
    
	public String detectorId() {
		return LayerViolationDetector.ID;
	}
	
    public boolean violationsDetected() {
        return !references.isEmpty();
    }
    
    public int numberOfViolations() {
        return references.size();
    }

}
