package org.principle.domain.checker;

import org.junit.Test;
import org.principle.app.service.DesignCheckService;
import org.principle.app.service.PackageAnalyzer;
import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.detector.cycledetector.CycleDetector;
import org.principle.domain.detector.layerviolationdetector.LayerViolationDetector;
import org.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.principle.infrastructure.service.jdepend.JDependRunner;
import org.principle.infrastructure.service.jdepend.PackageBuilder;

public class DesignCheckerTest {

    @Test
    public void checkItself() {
        String basePackage = "org.principle";
        DesingCheckerParameters parameters = new DesingCheckerParameters(basePackage, "infrastructure", "app", "domain");
        
        DesignCheckService designCheckService = getDesingCheckService(parameters);

        DesignCheckResults results = designCheckService.analyze(parameters);

        System.err.println(results.getErrorReport());

    }

    private DesignCheckService getDesingCheckService(DesingCheckerParameters parameters) {
        JDependRunner jDependRunner = new JDependRunner();
        PackageBuilder packageBuilder = new PackageBuilder();
        PackageAnalyzer packageAnalyzer = new JDependPackageAnalyzer(jDependRunner, packageBuilder);

        LayerViolationDetector layerViolationDetector = new LayerViolationDetector(parameters);
        CycleDetector cycleDetector = new CycleDetector(parameters.getBasePackage());
        DesignChecker designChecker = new DesignChecker(layerViolationDetector, cycleDetector);
        DesignCheckService designCheckService = new DesignCheckService(packageAnalyzer, designChecker);
        return designCheckService;
    }

}
