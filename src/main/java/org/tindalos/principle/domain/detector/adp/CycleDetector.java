package org.tindalos.principle.domain.detector.adp;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.CyclesInSubgraph;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.core.ReachedCyclesLimitException;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CycleDetector implements Detector {

    public static final String ID = "APDDetector";

    private final PackageStructureBuilder packageStructureBuilder;

    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public ADPResult analyze(CheckInput checkInput) {

        Package basePackage = packageStructureBuilder.build(checkInput.getPackages(), checkInput.getConfiguration()
                .getBasePackage());
        Map<PackageReference, Package> references = basePackage.toMap();

        Set<Cycle> cycleSet = Sets.newHashSet();

        List<Package> sortedByAfferents = orderByAfferents(references.values());
        if (basePackage.getMetrics().getAfferentCoupling() == 0) {
            sortedByAfferents.remove(basePackage);
        }
        // sortedByAfferents.add(basePackage); //add to the end
        try {
            while (!sortedByAfferents.isEmpty()) {
                // while (!sortedByAfferents.isEmpty() ||
                // sortedByAfferents.equals(Lists.newArrayList(basePackage))) {
                CyclesInSubgraph cyclesInSubgraph = sortedByAfferents.get(0).detectCycles(references);
                cycleSet.addAll(cyclesInSubgraph.getCycles());
                sortedByAfferents.removeAll(cyclesInSubgraph.getInvestigatedPackages());
            }
        } catch (ReachedCyclesLimitException ex) {
            cycleSet.addAll(ex.getCycles());
        }
        return new ADPResult(Lists.newArrayList(cycleSet), checkInput.getConfiguration().getExpectations()
                .getPackageCoupling().getADP());
    }

    public boolean isWanted(DesignQualityExpectations expectations) {
        PackageCoupling packageCoupling = expectations.getPackageCoupling();
        if (packageCoupling == null) {
            return false;
        }
        return packageCoupling.getADP() != null;
    }

    private static List<Package> orderByAfferents(Collection<Package> packages) {
        List<Package> temp = Lists.newArrayList(packages);
        Comparator<Package> comp = new Comparator<Package>() {

            public int compare(Package arg0, Package arg1) {
                return arg0.getMetrics().getAfferentCoupling().compareTo(arg1.getMetrics().getAfferentCoupling());
            }
        };
        Collections.sort(temp, comp);
        return temp;
    }
}
