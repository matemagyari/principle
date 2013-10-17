package org.tindalos.principle.domain.detector.cycledetector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.core.CheckInput;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.DesingCheckerParameters;
import org.tindalos.principle.domain.core.Detector;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;

public class CycleDetector implements Detector {
    
    private final PackageStructureBuilder packageStructureBuilder;
    
    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public List<Cycle> analyze(Collection<Package> packages, DesingCheckerParameters parameters) {
        
        Package basePackage = packageStructureBuilder.build(packages, parameters);
        Map<PackageReference, Package> references = basePackage.toMap();
        return basePackage.detectCycles(references);
    }

    public APDResult analyze(CheckInput checkInput) {
        List<Cycle> cycles = analyze(checkInput.getPackages(), checkInput.getParameters());
        return new APDResult(cycles);
    }
}
