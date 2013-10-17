package org.tindalos.principle.infrastructure.di;

import org.tindalos.principle.app.service.DesignCheckService;
import org.tindalos.principle.domain.checker.DesignChecker;
import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.detector.cycledetector.CycleDetector;
import org.tindalos.principle.domain.detector.cycledetector.PackageStructureBuilder;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationDetector;
import org.tindalos.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner;
import org.tindalos.principle.infrastructure.service.jdepend.PackageBuilder;

public class PoorMansDIContainer {
    
    
    public static DesignCheckService getDesignCheckService() {
        JDependRunner jDependRunner = new JDependRunner();
        PackageBuilder packageBuilder = new PackageBuilder();
        PackageAnalyzer packageAnalyzer = new JDependPackageAnalyzer(jDependRunner, packageBuilder);
        
        LayerViolationDetector layerViolationDetector = new LayerViolationDetector();
        PackageStructureBuilder packageStructureBuilder = new PackageStructureBuilder();
        CycleDetector cycleDetector = new CycleDetector(packageStructureBuilder);
        DesignChecker designChecker = new DesignChecker(layerViolationDetector, cycleDetector);
        return new DesignCheckService(packageAnalyzer, designChecker);
    }
}
