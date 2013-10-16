package org.principle.infrastructure.di;

import org.principle.app.service.DesignCheckService;
import org.principle.app.service.PackageAnalyzer;
import org.principle.domain.checker.DesignChecker;
import org.principle.domain.detector.cycledetector.CycleDetector;
import org.principle.domain.detector.cycledetector.PackageStructureBuilder;
import org.principle.domain.detector.layerviolationdetector.LayerViolationDetector;
import org.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.principle.infrastructure.service.jdepend.JDependRunner;
import org.principle.infrastructure.service.jdepend.PackageBuilder;

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
