package org.tindalos.principle.infrastructure.reporters;

import java.util.List;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.layering.LayerReference;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;

public class LayerViolationsReporter implements ViolationsReporter<LayerViolationsResult> {
    
    public String report(LayerViolationsResult result) {
        List<LayerReference> layerReferences = result.getViolations();
        String sectionLine = "==============================================================";
        StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
        sb.append("\tLayering violations ("+layerReferences.size()+" of allowed "+result.getThreshold()+" )\t");
        sb.append("\n" + sectionLine + "\n");
        
		if (layerReferences.isEmpty()) {
			sb.append("No violations.\n");
		} else {
			for (LayerReference layerReference : layerReferences) {
				sb.append(layerReference + "\n");
			}
		}
        sb.append(sectionLine + "\n");
        return sb.toString();
    }
    
    public Class<LayerViolationsResult> getType() {
        return LayerViolationsResult.class;
    }
}
