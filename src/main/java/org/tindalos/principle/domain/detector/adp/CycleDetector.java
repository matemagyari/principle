package org.tindalos.principle.domain.detector.adp;

import java.util.List;
import java.util.Map;

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

public class CycleDetector implements Detector {
	
	public static final String ID = "APDDetector";
    
    private final PackageStructureBuilder packageStructureBuilder;
    
    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public APDResult analyze(CheckInput checkInput) {
        Package basePackage = packageStructureBuilder.build(checkInput.getPackages(), checkInput.getConfiguration().getBasePackage());
        Map<PackageReference, Package> references = basePackage.toMap();
        List<Package> unreferredPackages = findUnreferred(checkInput.getPackages(), checkInput.getConfiguration().getBasePackage());
        System.err.println("unreferredPackages " + unreferredPackages);
        //List<Cycle> cycles = basePackage.detectCycles(references);
        List<Cycle> cycles = Lists.newArrayList();
        for (Package aPackage : unreferredPackages) {
        	cycles.addAll(aPackage.detectCycles(references));
		}

        return new APDResult(cycles, checkInput.getConfiguration().getExpectations().getPackageCoupling().getADP());
    }

	public boolean isWanted(DesignQualityExpectations expectations) {
		PackageCoupling packageCoupling = expectations.getPackageCoupling();
		if (packageCoupling == null) {
			return false;
		}
		return packageCoupling.getADP() != null;
	}
	
    
	private static List<Package> findUnreferred(List<Package> packages, final String basePackage) {
		Predicate<Package> filter = new Predicate<Package>() {
			public boolean apply(Package input) {
				return input.isUnreferred() && !input.getReference().getName().equals(basePackage);
			}
		};
		return Lists.newArrayList(Iterables.filter(packages, filter));
	}
}
