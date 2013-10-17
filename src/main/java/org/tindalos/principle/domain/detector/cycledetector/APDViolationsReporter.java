package org.tindalos.principle.domain.detector.cycledetector;

import java.util.List;

import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.reporting.ViolationsReporter;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

public class APDViolationsReporter implements ViolationsReporter {
    
    PoorMansDIContainer c;
    
    public String report(List<Cycle> cycles) {
        String sectionLine = "==============================================================";
        StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
        sb.append("\tAcyclic Package Dependency violations\t");
        sb.append("\n" + sectionLine + "\n");
        for (Cycle cycle : cycles) {
            sb.append(print(cycle) + "\n");
        }
        sb.append(sectionLine + "\n");
        return sb.toString();
    }

    private String print(Cycle cycle) {
        
        String arrow = "-->";
        StringBuffer sb = new StringBuffer("*"+arrow);
        for (PackageReference reference : cycle.getReferences()) {
            sb.append(reference + arrow);
        }
        return sb.append("*"). toString();
    }
    

}
