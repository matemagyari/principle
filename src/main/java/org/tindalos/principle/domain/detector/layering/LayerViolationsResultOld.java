package org.tindalos.principle.domain.detector.layering;

import java.util.List;

import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.expectations.Layering;

import com.google.common.collect.Lists;

public class LayerViolationsResultOld implements CheckResult {

    private final List<LayerReference> references;
	private final Layering layeringExpectations;

    public LayerViolationsResultOld(List<LayerReference> references, Layering layeringExpectations) {
        this.layeringExpectations = layeringExpectations;
		this.references = Lists.newArrayList(references);
    }
    
    public List<LayerReference> getViolations() {
        return references;
    }

	public boolean expectationsFailed() {
		return references.size() > layeringExpectations.getViolationsThreshold();
	}

    public Integer getThreshold() {
        return layeringExpectations.getViolationsThreshold();
    }

}
