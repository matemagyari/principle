package org.tindalos.principle.domain.detector.cycledetector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.Detector;

public class CycleDetector implements Detector<APDResult> {
	
	public static final String ID = "APDDetector";
    
    private final PackageStructureBuilder packageStructureBuilder;
    
    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    private List<Cycle> analyze(Collection<Package> packages, DesignCheckerParameters parameters) {
        
        Package basePackage = packageStructureBuilder.build(packages, parameters);
        Map<PackageReference, Package> references = basePackage.toMap();
        return basePackage.detectCycles(references);
    }

    public APDResult analyze(CheckInput checkInput) {
        List<Cycle> cycles = analyze(checkInput.getPackages(), checkInput.getParameters());
        return new APDResult(cycles);
    }
}
