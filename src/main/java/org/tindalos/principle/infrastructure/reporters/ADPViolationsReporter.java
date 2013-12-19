package org.tindalos.principle.infrastructure.reporters;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.adp.ADPResult;

import com.google.common.collect.Lists;

public class ADPViolationsReporter implements ViolationsReporter<ADPResult> {

    public String report(ADPResult result) {
        Map<PackageReference, Set<Cycle>> cyclesByBreakingPoints = result.getCyclesByBreakingPoints();
        String sectionLine = "==============================================================";
        StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
        sb.append("\tAcyclic Package Dependency Principle violations (" + cyclesByBreakingPoints.size() + " of the allowed "
                + result.getThreshold() + ")\t");
        sb.append("\n" + sectionLine + "\n");

        sb.append(sectionLine + "\n");

        if (cyclesByBreakingPoints.isEmpty()) {
            sb.append("No violations.\n");
        } else {
            sb.append("The cycles could be broken up refactoring the following packages: \n\n");
            for (Entry<PackageReference, Set<Cycle>> entry : cyclesByBreakingPoints.entrySet()) {
                sb.append(entry.getKey() + " ("+entry.getValue().size()+")\n");
            }

            for (Entry<PackageReference, Set<Cycle>> entry : cyclesByBreakingPoints.entrySet()) {
                sb.append("\nExample cycles caused by " + entry.getKey() + "\n");
                List<Cycle> cycleList = Lists.newArrayList( entry.getValue());
                Collections.sort(cycleList);
                for (Cycle cycle : cycleList) {
                    sb.append(print(cycle) + "\n");
                }
            }
        }

        sb.append(sectionLine + "\n");
        return sb.toString();
    }

    private String print(Cycle cycle) {

        String arrow = "-->";
        StringBuffer sb = new StringBuffer();
        for (PackageReference reference : cycle.getReferences()) {
            sb.append("\n" + reference + " " + arrow);
        }
        sb.append("\n-------------------------------");
        return sb.toString();
    }

    public Class<ADPResult> getType() {
        return ADPResult.class;
    }

}
