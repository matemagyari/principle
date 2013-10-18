package org.tindalos.principle.domain.detector.layerviolationdetector;

import java.util.List;

import org.tindalos.principle.domain.reporting.ViolationsReporter;

public class LayerViolationsReporter implements ViolationsReporter<LayerViolationsResult> {
    
    public String report(LayerViolationsResult result) {
        List<LayerReference> layerReferences = result.getViolations();
        String sectionLine = "==============================================================";
        StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
        sb.append("\tLayering violations\t");
        sb.append("\n" + sectionLine + "\n");
        for (LayerReference layerReference : layerReferences) {
            sb.append(layerReference + "\n");
        }
        sb.append(sectionLine + "\n");
        return sb.toString();
    }
    
    public Class<LayerViolationsResult> getType() {
        return LayerViolationsResult.class;
    }
}
