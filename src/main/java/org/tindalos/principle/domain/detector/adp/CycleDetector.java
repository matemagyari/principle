package org.tindalos.principle.domain.detector.adp;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CycleDetector implements Detector {

    public static final String ID = "APDDetector";

    private final PackageStructureBuilder packageStructureBuilder;

    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public APDResult analyze(CheckInput checkInput) {
        
        Package basePackage = packageStructureBuilder.build(checkInput.getPackages(), checkInput.getConfiguration()
                .getBasePackage());
        Map<PackageReference, Package> references = basePackage.toMap();
        
        Set<Cycle> cycleSet = Sets.newHashSet();

        /*
        List<Package> unreferredPackages = findUnreferred(checkInput.getPackages(), basePackage);
        List<Package> uncheckedPackages = Lists.newArrayList(checkInput.getPackages());
        uncheckedPackages.remove(basePackage);
        

        
        for (Package anUnreferredPackage : unreferredPackages) {
            cycleSet.addAll(anUnreferredPackage.detectCycles(references));
            Set<PackageReference> cumulatedDependencies = anUnreferredPackage.cumulatedDependencies(references);
            remove(uncheckedPackages, cumulatedDependencies);
            uncheckedPackages.remove(anUnreferredPackage);
        }
        
        if (!uncheckedPackages.isEmpty()) {
            cycleSet.addAll(basePackage.detectCycles(references));
        }
        */
        
        
        /*
         */
        List<Package> sortedByAfferents = orderByAfferents(checkInput.getPackages());
        sortedByAfferents.remove(basePackage);
        //sortedByAfferents.add(basePackage); //add to the end

        while (!sortedByAfferents.isEmpty()) {
           // while (!sortedByAfferents.isEmpty() || sortedByAfferents.equals(Lists.newArrayList(basePackage))) {
            Package aPackage = sortedByAfferents.get(0);
            cycleSet.addAll(aPackage.detectCycles(references));
            Set<PackageReference> cumulatedDependencies = aPackage.cumulatedDependencies(references);
            remove(sortedByAfferents, cumulatedDependencies);
            sortedByAfferents.remove(aPackage);
        }
        return new APDResult(Lists.newArrayList(cycleSet), checkInput.getConfiguration().getExpectations()
                .getPackageCoupling().getADP());
    }

    private void remove(List<Package> sortedByAfferents, Set<PackageReference> cumulatedDependencies) {
        List<Package> temp = Lists.newArrayList(sortedByAfferents);
        for (Package aPackage : temp) {
            if (cumulatedDependencies.contains(aPackage.getReference())) {
                sortedByAfferents.remove(aPackage);
            }
        }
    }

    public boolean isWanted(DesignQualityExpectations expectations) {
        PackageCoupling packageCoupling = expectations.getPackageCoupling();
        if (packageCoupling == null) {
            return false;
        }
        return packageCoupling.getADP() != null;
    }

    private static List<Package> findUnreferred(List<Package> packages, final Package basePackage) {
        Predicate<Package> filter = new Predicate<Package>() {
            public boolean apply(Package input) {
                return input.isUnreferred() && !input.equals(basePackage);
            }
        };
        return Lists.newArrayList(Iterables.filter(packages, filter));
    }

    private static List<Package> orderByAfferents(List<Package> packages) {
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
