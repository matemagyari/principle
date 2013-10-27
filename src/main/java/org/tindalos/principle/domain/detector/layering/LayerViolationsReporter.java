package org.tindalos.principle.domain.detector.layering;

import java.util.List;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;

public class LayerViolationsReporter implements ViolationsReporter<LayerViolationsResult> {
    
    public String report(LayerViolationsResult result) {
        List<LayerReference> layerReferences = result.getViolations();
        String sectionLine = "==============================================================";
        StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
        sb.append("\tLayering violations\t");
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
