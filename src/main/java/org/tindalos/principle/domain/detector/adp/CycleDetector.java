package org.tindalos.principle.domain.detector.adp;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.Detector;

public class CycleDetector implements Detector {
	
	public static final String ID = "APDDetector";
    
    private final PackageStructureBuilder packageStructureBuilder;
    
    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public APDResult analyze(CheckInput checkInput) {
        Package basePackage = packageStructureBuilder.build(checkInput.getPackages(), checkInput.getParameters());
        Map<PackageReference, Package> references = basePackage.toMap();
        List<Cycle> cycles = basePackage.detectCycles(references);
        return new APDResult(cycles);
    }
}
