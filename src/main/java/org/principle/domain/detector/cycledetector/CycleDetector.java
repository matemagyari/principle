package org.principle.domain.detector.cycledetector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.principle.domain.core.Cycle;
import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.core.Package;
import org.principle.domain.core.PackageReference;

public class CycleDetector {
    
    private final PackageStructureBuilder packageStructureBuilder;
    
    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public List<Cycle> analyze(Collection<Package> packages, DesingCheckerParameters parameters) {
        
        Package basePackage = packageStructureBuilder.build(packages, parameters);
        Map<PackageReference, Package> references = basePackage.toMap();
        return basePackage.detectCycles(references);
    }
}
