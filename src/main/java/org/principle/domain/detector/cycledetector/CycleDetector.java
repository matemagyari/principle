package org.principle.domain.detector.cycledetector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.principle.domain.detector.cycledetector.core.Cycle;
import org.principle.domain.detector.cycledetector.core.Package;
import org.principle.domain.detector.cycledetector.core.PackageReference;

public class CycleDetector {
    
    private final PackageStructureBuilder packageStructureBuilder;
    
    public CycleDetector(String basePackageName) {
        this.packageStructureBuilder = new PackageStructureBuilder(basePackageName);
    }

    public List<Cycle> analyze(Collection<Package> packages) {
        
        Package basePackage = packageStructureBuilder.build(packages);
        Map<PackageReference, Package> references = basePackage.toMap();
        return basePackage.detectCycles(references);
    }
}
