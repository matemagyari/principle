package org.tindalos.principle.domain.detector.layerviolationdetector;

import java.util.List;

import org.tindalos.principle.domain.reporting.ViolationsReporter;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

public class LayerViolationsReporter implements ViolationsReporter {
    
    PoorMansDIContainer c;
    
    public String report(List<LayerReference> layerReferences) {
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
    

}
